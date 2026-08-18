package id.perumdamts.kepegawaian.services.cuti.klaim;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanKlaimPostRequest;
import id.perumdamts.kepegawaian.dto.master.hariLibur.TanggalHariLibur;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.ECutiPeriod;
import id.perumdamts.kepegawaian.entities.commons.EReadWriteStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiApproval;
import id.perumdamts.kepegawaian.entities.cuti.CutiKlaimDetail;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.helpers.DateHelper;
import id.perumdamts.kepegawaian.helpers.RedisHelper;
import id.perumdamts.kepegawaian.helpers.cuti.CutiPeriodClassifier;
import id.perumdamts.kepegawaian.mapper.cuti.approval.CutiApprovalMapper;
import id.perumdamts.kepegawaian.mapper.cuti.pengajuan.CutiPegawaiMapper;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiApprovalChainRepository;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiKlaimDetailRepository;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiPegawaiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.HariLiburRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.services.cuti.CutiOwnershipService;
import id.perumdamts.kepegawaian.services.cuti.approvalChain.CutiApprovalChainGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KlaimCutiCommand {
    private final RedisHelper redisHelper;
    private final CutiPegawaiRepository cutiPegawaiRepository;
    private final CutiKlaimValidator cutiKlaimValidator;
    private final HariLiburRepository hariLiburRepository;
    private final CutiKlaimDetailRepository cutiKlaimDetailRepository;
    private final CutiApprovalChainRepository cutiApprovalChainRepository;
    private final PegawaiRepository pegawaiRepository;
    private final CutiApprovalChainGenerator cutiApprovalChainGenerator;
    private final CutiApproveKlaimCutiService cutiApproveKlaimCutiService;
    private final CutiKlaimCrossYearSettlement cutiKlaimCrossYearSettlement;
    private final CutiOwnershipService ownershipService;

    @Value("${custom.jabatan.supervisorSdm}")
    private Long supervisorSdm;

    @Transactional
    public SavedStatus<Long> save(CutiPengajuanKlaimPostRequest request) {
        // ADR-0038: non-ADMIN/HRD wajib klaim atas nama sendiri
        ownershipService.resolvePemohon(request.getPegawaiId());
        CutiPegawai validCuti = cutiKlaimValidator.validateKlaim(request);
        CutiPegawai entity = CutiPegawaiMapper.toEntity(validCuti, request);
        entity.setPicSaatIni(new Jabatan(supervisorSdm));

        List<LocalDate> working = setWorkingDays(request.getListHari(), entity);
        CutiPegawai save = cutiPegawaiRepository.save(entity);
        saveDetails(save, working);

        cutiApprovalChainGenerator.forKlaim(save);
        return SavedStatus.build(ESaveStatus.SUCCESS, save.getId());
    }

    @Transactional
    public SavedStatus<Long> update(Long id, CutiPengajuanKlaimPostRequest request) {
        CutiPegawai cutiPegawai = cutiPegawaiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unknown Cuti Pegawai"));
        // non-ADMIN/HRD hanya boleh update klaim milik sendiri
        ownershipService.assertOwns(cutiPegawai.getPegawai().getId());

        List<LocalDate> working = setWorkingDays(request.getListHari(), cutiPegawai);
        CutiPegawai save = cutiPegawaiRepository.save(cutiPegawai);

        List<CutiKlaimDetail> klaimDetails = cutiKlaimDetailRepository.findByRefCuti_id(id);
        cutiKlaimDetailRepository.deleteAll(klaimDetails);
        saveDetails(save, working);

        return SavedStatus.build(ESaveStatus.SUCCESS, save.getId());
    }

    @Transactional
    public SavedStatus<String> saveKlaim(CutiApprovalPostRequest request) {
        if (redisHelper.isTokenAlreadyUsed(request.getCsrfToken())) {
            throw new ConflictException("Duplicate request detected");
        }

        CutiPegawai cutiPegawai = cutiPegawaiRepository.findById(request.getCutiId())
                .orElseThrow(() -> new RuntimeException("Unknown Cuti Pegawai"));
        Pegawai approver = pegawaiRepository.findById(request.getApproverId())
                .orElseThrow(() -> new RuntimeException("Approver Pegawai not found"));

        if (!cutiPegawai.getPicSaatIni().equals(approver.getJabatan())) {
            throw new RuntimeException("Approver Pegawai not found");
        }

        CutiApproval entity = CutiApprovalMapper.toEntity(request, cutiPegawai, approver);
        cutiPegawai.setApprovalCutiStatus(request.getApprovalStatus());

        if (!request.getApprovalStatus().equals(EApprovalCutiStatus.APPROVED)) {
            cutiPegawaiRepository.save(cutiPegawai);
        } else {
            // kepegawaian-ciw: settlement klaim didispatch berdasarkan PERIODE refCuti
            // (keputusan dibuat saat pengajuan), bukan re-klasifikasi tanggal klaim dengan
            // now() — yang dulu bisa crash (IllegalArgumentException) / salah bucket saat
            // approval lintas tahun. Fallback defensif: tanggal klaim sendiri.
            CutiPegawai refCuti = cutiPegawai.getRefCuti();
            ECutiPeriod period = refCuti != null
                    ? CutiPeriodClassifier.resolvePeriod(refCuti.getTanggalMulai(), refCuti.getTanggalSelesai(), refCuti.getCreatedAt())
                    : CutiPeriodClassifier.resolvePeriod(cutiPegawai.getTanggalMulai(), cutiPegawai.getTanggalSelesai(), cutiPegawai.getCreatedAt());
            switch (period) {
                case NEXT_YEAR -> cutiKlaimCrossYearSettlement.forNextYear(cutiPegawai, entity);
                case OVERLAPPING -> cutiKlaimCrossYearSettlement.overlappingYear(cutiPegawai, entity);
                case JAN_JUN -> cutiApproveKlaimCutiService.between1JanAnd30Jun(cutiPegawai, entity);
                case JUL_DES -> cutiApproveKlaimCutiService.between1JulAnd31Dec(cutiPegawai, entity);
                case JUN_JUL -> cutiApproveKlaimCutiService.between30JunAnd1Jul(cutiPegawai, entity);
            }
        }
        cutiApprovalChainRepository.findByRefCutiIdAndJabatanId(cutiPegawai.getId(), approver.getJabatan().getId())
                .ifPresent(chain -> {
                    chain.setReadWriteStatus(EReadWriteStatus.READ);
                    chain.setApprovalStatus(request.getApprovalStatus());
                    cutiApprovalChainRepository.save(chain);
                });

        return SavedStatus.build(ESaveStatus.SUCCESS, "success");
    }

    private List<LocalDate> setWorkingDays(List<LocalDate> listHari, CutiPegawai entity) {
        List<LocalDate> libur = hariLiburRepository
                .findByTanggalBetween(listHari.getFirst(), listHari.getLast())
                .stream().map(TanggalHariLibur::getTanggal).toList();
        List<LocalDate> working = DateHelper.getWorkingDays(listHari, libur);
        entity.setJumlahHari(working.size());
        entity.setJumlahHariKerja(working.size());
        return working;
    }

    private void saveDetails(CutiPegawai cuti, List<LocalDate> dates) {
        List<CutiKlaimDetail> details = dates.stream()
                .map(date -> new CutiKlaimDetail(cuti, date))
                .toList();
        cutiKlaimDetailRepository.saveAll(details);
    }
}
