package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.config.CutiProperties;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.*;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EReadWriteStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;
import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.helpers.DateHelper;
import id.perumdamts.kepegawaian.helpers.RedisHelper;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiJenisRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiPegawaiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.HariLiburRepository;
import id.perumdamts.kepegawaian.services.cuti.approvalChain.CutiApprovalChainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CutiPengajuanServiceImpl implements CutiPengajuanService {
    private final RedisHelper redisHelper;
    private final CutiPegawaiRepository repository;
    private final CutiProperties cutiProperties;
    private final SaveCutiService saveCutiService;
    private final HariLiburRepository hariLiburRepository;
    private final PegawaiRepository pegawaiRepository;
    private final CutiJenisRepository cutiJenisRepository;
    private final CutiApprovalChainService cutiApprovalChainService;
    private final SaveKlaimCutiService klaimCutiService;
    private final CutiPengajuanValidator cutiPengajuanValidator;
    private final PengajuanCutiCommand pengajuanCutiCommand;

    @Override
    public Page<CutiPengajuanResponse> findPage(CutiPengajuanRequest request) {

        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(CutiPengajuanResponse::from);
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
            return pengajuanCutiCommand.save(request);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> update(Long id, CutiPengajuanPutRequest request) {
        try {
            return pengajuanCutiCommand.update(id, request);
        } catch (Exception e) {
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
//        if (redisHelper.validateToken(request.getCsrfToken())) {
//            // Return a duplicate status if the token is already used
//            return SavedStatus.build(ESaveStatus.DUPLICATE, "Duplicate request detected");
//        }
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
        try {
            return pengajuanCutiCommand.pembatalan(id);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
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
