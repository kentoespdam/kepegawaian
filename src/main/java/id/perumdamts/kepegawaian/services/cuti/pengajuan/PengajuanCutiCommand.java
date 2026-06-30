package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.config.CutiProperties;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanPutRequest;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EReadWriteStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;
import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.helpers.DateHelper;
import id.perumdamts.kepegawaian.helpers.RedisHelper;
import id.perumdamts.kepegawaian.repositories.cuti.CutiJenisRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiPegawaiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.HariLiburRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.services.cuti.approvalChain.CutiApprovalChainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    private final CutiApprovalChainService cutiApprovalChainService;
    private final CutiPengajuanValidator cutiPengajuanValidator;

    @Transactional
    public SavedStatus<?> save(CutiPengajuanPostRequest request) {
        if (redisHelper.validateToken(request.getCsrfToken())) {
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Duplicate request detected");
        }
        cutiPengajuanValidator.validate(request);

        Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId())
                .orElseThrow(() -> new RuntimeException("Unknown Pegawai"));
        CutiJenis jenisCuti = cutiJenisRepository.getReferenceById(request.getJenisCutiId());
        CutiJenis subJenisCuti = request.getSubJenisCutiId() != null
                ? cutiJenisRepository.getReferenceById(request.getSubJenisCutiId())
                : null;

        int nowYear = LocalDate.now().getYear();
        LocalDate tanggalMulai = request.getTanggalMulai();
        LocalDate tanggalSelesai = request.getTanggalSelesai();
        int startYear = tanggalMulai.getYear();
        int endYear = tanggalSelesai.getYear();

        int totalDays = DateHelper.countWeekdaysBetween(request.getTanggalMulai(), request.getTanggalSelesai());
        int totalHariCuti = totalDays - hariLiburRepository.countByTanggalBetween(request.getTanggalMulai(), request.getTanggalSelesai());

        CutiPegawai entity = CutiPengajuanPostRequest.toEntity(request, pegawai, jenisCuti, subJenisCuti);
        entity.setJumlahHari(totalDays);
        entity.setJumlahHariKerja(totalHariCuti);

        // CUTI TAHUNAN
        if (request.getJenisCutiId().equals(cutiProperties.getJenisCutiTahunan())) {
            CutiPegawai cutiPegawai;
            if (startYear > nowYear && endYear > nowYear) {
                cutiPegawai = saveCutiService.forNextYear(request, entity);
            } else if (startYear == nowYear && endYear > startYear) {
                cutiPegawai = saveCutiService.overlappingYear(request, entity);
            } else if (DateHelper.isBetweenDates(tanggalMulai, tanggalSelesai, startYear, 1, 6, 30)) {
                cutiPegawai = saveCutiService.between1JanAnd30Jun(request, entity);
            } else if (DateHelper.isBetweenDates(tanggalMulai, tanggalSelesai, startYear, 7, 12, 31)) {
                cutiPegawai = saveCutiService.between1JulAnd31Dec(request, entity);
            } else if (DateHelper.isOverlappingDates(tanggalMulai, tanggalSelesai, startYear)) {
                cutiPegawai = saveCutiService.between30JunAnd1Jul(request, entity);
            } else {
                throw new RuntimeException("Invalid request");
            }

            List<CutiApprovalChain> cutiApprovalChains = cutiApprovalChainService.generateApprovalChain(cutiPegawai);
            Optional<CutiApprovalChain> writeChain = cutiApprovalChains.stream()
                    .filter(chain -> chain.getReadWriteStatus().equals(EReadWriteStatus.WRITE))
                    .findFirst();
            if (writeChain.isPresent()) {
                CutiApprovalChain writeChainEntity = writeChain.get();
                cutiPegawai.setApprovalLevel(writeChainEntity.getApprovalLevel());
                cutiPegawai.setPicSaatIni(new Jabatan(writeChainEntity.getJabatanId()));
                repository.save(cutiPegawai);
            }

        } else {
            saveCutiService.saveCutiNonTahunan(request, entity);
        }
        return SavedStatus.build(ESaveStatus.SUCCESS, "Cuti Pengajuan berhasil disimpan");
    }

    @Transactional
    public SavedStatus<?> update(Long id, CutiPengajuanPutRequest request) {
        if (redisHelper.validateToken(request.getCsrfToken())) {
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Duplicate request detected");
        }
        CutiPegawai cutiPegawai = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unknown Cuti Pengajuan"));
        Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId())
                .orElseThrow(() -> new RuntimeException("Unknown Pegawai"));
        CutiJenis jenisCuti = cutiJenisRepository.getReferenceById(request.getJenisCutiId());
        CutiJenis subJenisCuti = request.getSubJenisCutiId() != null
                ? cutiJenisRepository.getReferenceById(request.getSubJenisCutiId())
                : null;

        int nowYear = LocalDate.now().getYear();
        LocalDate tanggalMulai = request.getTanggalMulai();
        LocalDate tanggalSelesai = request.getTanggalSelesai();
        int startYear = tanggalMulai.getYear();
        int endYear = tanggalSelesai.getYear();

        int totalDays = DateHelper.countWeekdaysBetween(request.getTanggalMulai(), request.getTanggalSelesai());
        int totalHariCuti = totalDays - hariLiburRepository.countByTanggalBetween(request.getTanggalMulai(), request.getTanggalSelesai());

        CutiPegawai entity = CutiPengajuanPutRequest.toEntity(cutiPegawai, request, pegawai, jenisCuti, subJenisCuti);
        entity.setJumlahHari(totalDays);
        entity.setJumlahHariKerja(totalHariCuti);

        // CUTI TAHUNAN
        if (request.getJenisCutiId().equals(cutiProperties.getJenisCutiTahunan())) {
            if (startYear > nowYear && endYear > nowYear) {
                saveCutiService.forNextYear(request, entity);
            } else if (startYear == nowYear && endYear > startYear) {
                saveCutiService.overlappingYear(request, entity);
            } else if (DateHelper.isBetweenDates(tanggalMulai, tanggalSelesai, startYear, 1, 6, 30)) {
                saveCutiService.between1JanAnd30Jun(request, entity);
            } else if (DateHelper.isBetweenDates(tanggalMulai, tanggalSelesai, startYear, 7, 12, 31)) {
                saveCutiService.between1JulAnd31Dec(request, entity);
            } else if (DateHelper.isOverlappingDates(tanggalMulai, tanggalSelesai, startYear)) {
                saveCutiService.between30JunAnd1Jul(request, entity);
            }
        } else {
            saveCutiService.saveCutiNonTahunan(request, entity);
        }

        return SavedStatus.build(ESaveStatus.SUCCESS, "Cuti Pengajuan berhasil di update");
    }

    @Transactional
    public SavedStatus<?> pembatalan(Long id) {
        CutiPegawai entity = repository.findByIdAndApprovalCutiStatus(id, EApprovalCutiStatus.PENDING)
                .orElseThrow(() -> new RuntimeException("Unknown Cuti Pegawai"));

        entity.setApprovalCutiStatus(EApprovalCutiStatus.CANCELED);
        repository.save(entity);

        return SavedStatus.build(ESaveStatus.SUCCESS, "Cuti Pengajuan berhasil dibatalkan");
    }
}
