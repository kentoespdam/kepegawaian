package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.config.DefConfig;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.*;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.helpers.DateHelper;
import id.perumdamts.kepegawaian.helpers.RedisHelper;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiJenisRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiPegawaiRepository;
import id.perumdamts.kepegawaian.repositories.master.HariLiburRepository;
import id.perumdamts.kepegawaian.services.cuti.approvalChain.CutiApprovalChainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CutiPengajuanServiceImpl implements CutiPengajuanService {
    private final RedisHelper redisHelper;
    private final CutiPegawaiRepository repository;
    private final DefConfig defConfig;
    private final SaveCutiService saveCutiService;
    private final HariLiburRepository hariLiburRepository;
    private final PegawaiRepository pegawaiRepository;
    private final CutiJenisRepository cutiJenisRepository;
    private final CutiApprovalChainService cutiApprovalChainService;
    private final SaveKlaimCutiService klaimCutiService;
    private final ValidatePengajuanCutiService validatePengajuanCutiService;

    @Value("${custom.jabatan.supervisorSdm}")
    private Long supervisorSdm;

    @Override
    public Page<CutiPengajuanResponse> findPage(CutiPengajuanRequest request) {

        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(CutiPengajuanResponse::from);
    }

    @Override
    public Page<CutiPengajuanResponse> findPageApproval(CutiPengajuanApprovalRequest request) {
        if (request.getPicSaatIniId().equals(supervisorSdm)) {
            Page<CutiPegawai> result = repository.findForApproval(request.getTahun(), request.getPicSaatIniId(), request.getApprovalCutiStatus().getValue(), request.getPageable());
            return result.map(CutiPengajuanResponse::from);
        }
        return cutiApprovalChainService.findCutiPegawai(request);
    }

    @Override
    public CutiPengajuanResponse findById(Long id) {
        return repository.findById(id).map(CutiPengajuanResponse::from)
                .orElse(null);
    }

    /**
     * Menyimpan pengajuan cuti pegawai.
     *
     * @param request pengajuan cuti pegawai yang akan disimpan
     * @return SavedStatus data
     */
    @Override
    public SavedStatus<?> save(CutiPengajuanPostRequest request) {
        try {
            if (redisHelper.validateToken(request.getCsrfToken())) {
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Duplicate request detected");
            }
            validatePengajuanCutiService.validate(request);

            Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Pegawai"));
            CutiJenis jenisCuti = cutiJenisRepository.findById(request.getJenisCutiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Jenis Cuti"));
            CutiJenis subJenisCuti = cutiJenisRepository.findById(request.getSubJenisCutiId())
                    .orElse(null);

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
            if (request.getJenisCutiId().equals(defConfig.getJenisCutiTahunan())) {
                CutiPegawai cutiPegawai = null;
                if (startYear > nowYear && endYear > nowYear) {
                    // pengajuan cuti tahunan untuk tahun depan
                    cutiPegawai = saveCutiService.forNextYear(request, entity);
                } else if (startYear == nowYear && endYear > startYear) {
                    // pengajuan cuti menyebrang tahun
                    cutiPegawai = saveCutiService.overlappingYear(request, entity);
                } else if (DateHelper.isBetweenDates(tanggalMulai, tanggalSelesai, startYear, 1, 6, 30)) {
                    // pengajuan cuti antara 1jan sampai 30 juni
                    cutiPegawai = saveCutiService.between1JanAnd30Jun(request, entity);
                } else if (DateHelper.isBetweenDates(tanggalMulai, tanggalSelesai, startYear, 7, 12, 31)) {
                    // pengajuan cuti antara 1 juli sampai 31 desember
                    cutiPegawai = saveCutiService.between1JulAnd31Dec(request, entity);
                } else if (DateHelper.isOverlappingDates(tanggalMulai, tanggalSelesai, startYear)) {
                    // pengajuan cuti antara tanggal 30 juni sampai 1 juli
                    cutiPegawai = saveCutiService.between30JunAnd1Jul(request, entity);
                }

                cutiApprovalChainService.generateApprovalChain(cutiPegawai);
            } else {
                saveCutiService.saveCutiNonTahunan(request, entity);
            }
            return SavedStatus.build(ESaveStatus.SUCCESS, "Cuti Pengajuan berhasil disimpan");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    /**
     * Membaharui data pengajuan cuti pegawai berdasarkan id.
     * <p>
     * Jika pengajuan cuti pegawai berhasil diubah, maka akan mengembalikan
     * objek {@link SavedStatus} dengan status {@link ESaveStatus#SUCCESS}.
     * Jika terjadi kesalahan, maka akan mengembalikan objek {@link SavedStatus}
     * dengan status {@link ESaveStatus#FAILED} dan pesan error.
     *
     * @param id      id pengajuan cuti pegawai yang akan diubah
     * @param request data pengajuan cuti yang akan diubah
     * @return SavedStatus data
     * @throws RuntimeException jika data pengajuan cuti tidak ditemukan
     */
    @Override
    public SavedStatus<?> update(Long id, CutiPengajuanPutRequest request) {
        try {
            if (redisHelper.validateToken(request.getCsrfToken()))
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Duplicate request detected");
            CutiPegawai cutiPegawai = repository.findById(id).orElseThrow(() -> new RuntimeException("Unknown Cuti Pengajuan"));
            Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Pegawai"));
            CutiJenis jenisCuti = cutiJenisRepository.findById(request.getJenisCutiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Jenis Cuti"));
            CutiJenis subJenisCuti = cutiJenisRepository.findById(request.getSubJenisCutiId())
                    .orElse(null);

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
            if (request.getJenisCutiId().equals(defConfig.getJenisCutiTahunan())) {
                if (startYear > nowYear && endYear > nowYear) {
                    // update cuti tahunan untuk tahun depan
                    saveCutiService.forNextYear(request, entity);
                } else if (startYear == nowYear && endYear > startYear) {
                    // update cuti menyebrang tahun
                    saveCutiService.overlappingYear(request, entity);
                } else if (DateHelper.isBetweenDates(tanggalMulai, tanggalSelesai, startYear, 1, 6, 30)) {
                    // update cuti antara 1jan sampai 30 juni
                    saveCutiService.between1JanAnd30Jun(request, entity);
                } else if (DateHelper.isBetweenDates(tanggalMulai, tanggalSelesai, startYear, 7, 12, 31)) {
                    // update cuti antara 1 juli sampai 31 desember
                    saveCutiService.between1JulAnd31Dec(request, entity);
                } else if (DateHelper.isOverlappingDates(tanggalMulai, tanggalSelesai, startYear)) {
                    // update cuti antara tanggal 30 juni sampai 1 juli
                    saveCutiService.between30JunAnd1Jul(request, entity);
                }
            } else {
                // update cuti non tahunan
                saveCutiService.saveCutiNonTahunan(request, entity);
            }

            return SavedStatus.build(ESaveStatus.SUCCESS, "Cuti Pengajuan berhasil di update");
        } catch (RuntimeException e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    /**
     * Processes a leave claim request for an employee.
     * This method validates the CSRF token to prevent duplicate submissions
     * and proceeds to save the leave claim request via the klaimCutiService.
     *
     * @param request the leave claim request data
     * @return the status of the save operation, indicating success or duplication
     */
    @Override
    public SavedStatus<?> klaim(CutiPengajuanKlaimPostRequest request) {
        // Validate the CSRF token to prevent duplicate requests
        if (redisHelper.validateToken(request.getCsrfToken())) {
            // Return a duplicate status if the token is already used
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Duplicate request detected");
        }
        // Save the leave claim request using the klaimCutiService
        return klaimCutiService.save(request);
    }

    /**
     * Updates an existing leave claim request for an employee.
     * This method validates the CSRF token to prevent duplicate submissions
     * and proceeds to update the leave claim request via the klaimCutiService.
     *
     * @param id      the id of the leave claim request to update
     * @param request the updated leave claim request data
     * @return the status of the update operation, indicating success or duplication
     */
    @Override
    public SavedStatus<?> updateKlaim(Long id, CutiPengajuanKlaimPostRequest request) {
        // Validate the CSRF token to prevent duplicate submissions
        if (redisHelper.validateToken(request.getCsrfToken())) {
            // Return a duplicate status if the token is already used
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Duplicate request detected");
        }
        // Update the leave claim request using the klaimCutiService
        return klaimCutiService.update(id, request);
    }

    /**
     * Membatalkan pengajuan cuti pegawai berdasarkan id.
     * <p>
     * Jika pengajuan cuti pegawai berhasil dibatalkan, maka akan mengembalikan
     * objek {@link SavedStatus} dengan status {@link ESaveStatus#SUCCESS}.
     * Jika terjadi kesalahan, maka akan mengembalikan objek {@link SavedStatus}
     * dengan status {@link ESaveStatus#FAILED} dan pesan error.
     *
     * @param id id pengajuan cuti pegawai yang akan dibatalkan
     * @return SavedStatus data
     * @throws RuntimeException jika data pengajuan cuti tidak ditemukan
     */
    @Override
    public SavedStatus<?> pembatalan(Long id) {
        // Find the leave request by id and approval status PENDING
        Optional<CutiPegawai> entity = repository.findByIdAndApprovalCutiStatus(id, EApprovalCutiStatus.PENDING);
        if (entity.isEmpty()) {
            // Return a failed status if the leave request is not found
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Cuti Pegawai");
        }

        // Set the approval status to CANCELED
        entity.get().setApprovalCutiStatus(EApprovalCutiStatus.CANCELED);

        // Save the updated leave request
        repository.save(entity.get());

        // Return a success status
        return SavedStatus.build(ESaveStatus.SUCCESS, "Cuti Pengajuan berhasil dibatalkan");
    }

    /**
     * Finds the total number of working days between the given start and end dates.
     * <p>
     * This method counts the number of weekdays (Monday to Friday) between the given
     * start and end dates, and subtracts the number of holidays that fall within
     * the given date range.
     *
     * @param tanggalMulai   the start date of the period
     * @param tanggalSelesai the end date of the period
     * @return the total number of working days
     */
    @Override
    public Integer findTotalHariKerja(LocalDate tanggalMulai, LocalDate tanggalSelesai) {
        int totalDays = DateHelper.countWeekdaysBetween(tanggalMulai, tanggalSelesai);
        return totalDays - hariLiburRepository.countByTanggalBetween(tanggalMulai, tanggalSelesai);
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }
}
