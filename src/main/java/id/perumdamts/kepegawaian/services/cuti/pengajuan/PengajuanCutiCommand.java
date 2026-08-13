package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.config.CutiProperties;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanPutRequest;
import id.perumdamts.kepegawaian.dto.master.hariLibur.TanggalHariLibur;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.ECutiPeriod;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.helpers.RedisHelper;
import id.perumdamts.kepegawaian.helpers.cuti.CutiPeriodClassifier;
import id.perumdamts.kepegawaian.helpers.cuti.WorkdayCalculator;
import id.perumdamts.kepegawaian.mapper.cuti.pengajuan.CutiPegawaiMapper;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiJenisRepository;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiPegawaiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.HariLiburRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.services.cuti.CutiOwnershipService;
import id.perumdamts.kepegawaian.services.cuti.approvalChain.CutiApprovalChainGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PengajuanCutiCommand {
    private final RedisHelper redisHelper;
    private final CutiPegawaiRepository repository;
    private final CutiProperties cutiProperties;
    private final SaveCutiService saveCutiService;
    private final HariLiburRepository hariLiburRepository;
    private final PegawaiRepository pegawaiRepository;
    private final CutiJenisRepository cutiJenisRepository;
    private final CutiApprovalChainGenerator cutiApprovalChainGenerator;
    private final CutiPengajuanValidator cutiPengajuanValidator;
    private final CutiOwnershipService ownershipService;

    @Transactional
    public SavedStatus<Long> save(CutiPengajuanPostRequest request) {
        if (redisHelper.isTokenAlreadyUsed(request.getCsrfToken())) {
            throw new ConflictException("Duplicate request detected");
        }
        // ADR-0038 + kepegawaian-p6np: ownership di-resolve PERTAMA dari principal
        // (non-ADMIN/HRD wajib atas nama sendiri -> 403 di sini), baru validator jalan.
        // Urutan ini mencegah error validator membocorkan status cuti pegawai lain.
        var pegawai = ownershipService.resolvePemohon(request.getPegawaiId());
        cutiPengajuanValidator.validate(request, pegawai.getId());

        var jenisCuti = cutiJenisRepository.getReferenceById(request.getJenisCutiId());
        var subJenisCuti = request.getSubJenisCutiId() != null ? cutiJenisRepository.getReferenceById(request.getSubJenisCutiId()) : null;

        int nowYear = LocalDate.now().getYear();
        Set<LocalDate> holidays = hariLiburRepository.findByTanggalBetween(request.getTanggalMulai(), request.getTanggalSelesai())
                .stream().map(TanggalHariLibur::getTanggal).collect(Collectors.toSet());
        int totalHariCuti = WorkdayCalculator.count(request.getTanggalMulai(), request.getTanggalSelesai(), holidays);

        CutiPegawai entity = CutiPegawaiMapper.toEntity(request, pegawai, jenisCuti, subJenisCuti);
        entity.setJumlahHari(totalHariCuti);
        entity.setJumlahHariKerja(totalHariCuti);

        if (request.getJenisCutiId().equals(cutiProperties.getJenisCutiTahunan())) {
            ECutiPeriod period = CutiPeriodClassifier.classify(request.getTanggalMulai(), request.getTanggalSelesai(), nowYear);
            CutiPegawai cutiPegawai = switch (period) {
                case NEXT_YEAR -> saveCutiService.forNextYear(request, entity);
                case OVERLAPPING -> saveCutiService.overlappingYear(request, entity);
                case JAN_JUN -> saveCutiService.between1JanAnd30Jun(request, entity);
                case JUL_DES -> saveCutiService.between1JulAnd31Dec(request, entity);
                case JUN_JUL -> saveCutiService.between30JunAnd1Jul(request, entity);
            };
            cutiApprovalChainGenerator.forPengajuan(cutiPegawai);
        } else {
            saveCutiService.saveCutiNonTahunan(request, entity);
        }
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional
    public SavedStatus<Long> update(Long id, CutiPengajuanPutRequest request) {
        if (redisHelper.isTokenAlreadyUsed(request.getCsrfToken())) {
            throw new ConflictException("Duplicate request detected");
        }
        // kepegawaian-3o6c: hanya cuti PENDING yang boleh di-update (konsisten dgn pembatalan)
        var cutiPegawai = repository.findByIdAndApprovalCutiStatus(id, EApprovalCutiStatus.PENDING)
                .orElseThrow(() -> new RuntimeException("Unknown Cuti Pengajuan"));
        // kepegawaian-hyq0: ownership WAJIB cek pemilik ENTITY, bukan hanya request pegawaiId
        // (updateEntity mempertahankan pegawai asli — tanpa ini USER bisa mengubah cuti orang lain)
        ownershipService.assertOwns(cutiPegawai.getPegawai().getId());
        // ADR-0038: request pegawaiId juga wajib milik sendiri untuk non-privileged
        var pegawai = ownershipService.resolvePemohon(request.getPegawaiId());
        // kepegawaian-3o6c: validasi update-aware, kuota/jenis/besar-ibadah dicek terhadap
        // pemilik cuti, exclude cuti yang sedang di-update dari cek pending-duplikat
        cutiPengajuanValidator.validate(request, cutiPegawai.getPegawai().getId(), id);
        var jenisCuti = cutiJenisRepository.getReferenceById(request.getJenisCutiId());
        var subJenisCuti = request.getSubJenisCutiId() != null ? cutiJenisRepository.getReferenceById(request.getSubJenisCutiId()) : null;

        int nowYear = LocalDate.now().getYear();
        Set<LocalDate> holidays = hariLiburRepository.findByTanggalBetween(request.getTanggalMulai(), request.getTanggalSelesai())
                .stream().map(TanggalHariLibur::getTanggal).collect(Collectors.toSet());
        int totalHariCuti = WorkdayCalculator.count(request.getTanggalMulai(), request.getTanggalSelesai(), holidays);

        CutiPegawai entity = CutiPegawaiMapper.updateEntity(cutiPegawai, request, pegawai, jenisCuti, subJenisCuti);
        entity.setJumlahHari(totalHariCuti);
        entity.setJumlahHariKerja(totalHariCuti);

        if (request.getJenisCutiId().equals(cutiProperties.getJenisCutiTahunan())) {
            ECutiPeriod period = CutiPeriodClassifier.classify(request.getTanggalMulai(), request.getTanggalSelesai(), nowYear);
            switch (period) {
                case NEXT_YEAR -> saveCutiService.forNextYear(request, entity);
                case OVERLAPPING -> saveCutiService.overlappingYear(request, entity);
                case JAN_JUN -> saveCutiService.between1JanAnd30Jun(request, entity);
                case JUL_DES -> saveCutiService.between1JulAnd31Dec(request, entity);
                case JUN_JUL -> saveCutiService.between30JunAnd1Jul(request, entity);
            }
        } else {
            saveCutiService.saveCutiNonTahunan(request, entity);
        }
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional
    public boolean pembatalan(Long id) {
        var entity = repository.findByIdAndApprovalCutiStatus(id, EApprovalCutiStatus.PENDING)
                .orElseThrow(() -> new RuntimeException("Unknown Cuti Pegawai"));
        // non-ADMIN/HRD hanya boleh membatalkan cuti milik sendiri
        ownershipService.assertOwns(entity.getPegawai().getId());
        entity.setApprovalCutiStatus(EApprovalCutiStatus.CANCELED);
        repository.save(entity);
        return true;
    }
}
