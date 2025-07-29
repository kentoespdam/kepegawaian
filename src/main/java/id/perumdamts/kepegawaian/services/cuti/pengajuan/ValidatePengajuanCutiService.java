package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.config.DefConfig;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanKlaimPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.repositories.cuti.CutiPegawaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ValidatePengajuanCutiService {
    private final CutiPegawaiRepository repository;
    private final DefConfig defConfig;

    /**
     * Validate the leave request. This validation is done to check if the employee
     * requesting the leave has any pending leave request, if the employee has
     * already taken a long leave, or if the employee has taken a leave for
     * performing a religious obligation.
     *
     * @param request the request body for the leave request
     */
    public void validate(CutiPengajuanPostRequest request) {
        // Check if the employee has any pending leave request
        boolean existsPendingPengajuan = repository.exists(request.getPendingStatusSpecification());
        if (existsPendingPengajuan) {
            throw new RuntimeException("Masih ada pengajuan cuti yang belum diapprove");
        }
        // Check if the employee has taken a long leave
        boolean existBesar = repository.exists(request.getSpecificationByJenisCuti(defConfig.getJenisCutiBesar(), request.getTanggalMulai()));
        if (existBesar) {
            throw new RuntimeException("Anda tidak berhak cuti tahunan karena telah mengambil cuti besar");
        }
        // Check if the employee has taken a leave for performing a religious obligation
        boolean existIbadah = repository.exists(request.getSpecificationByJenisCuti(defConfig.getJenisCutiIbadah()));
        if (existIbadah) {
            throw new RuntimeException("Anda tidak berhak cuti tahunan karena telah mengambil cuti melaksanakan ibadah");
        }
    }

    /**
     * Validates the minimal cuti which is 3 days. If the total days of leave is
     * less than 3, this method will throw an exception. This method is used to
     * validate the minimal cuti for a leave request.
     *
     * @param totalHariKerja the total days of leave
     * @param totalSisaKuota the total remaining quota of the year
     */
    public void validateMinimalCuti(int totalHariKerja, int totalSisaKuota) {
        // Check if the total days of leave is less than 3
        if (totalHariKerja < 3) {
            // If the total remaining quota of the year is more than 3
            if (totalSisaKuota >= 3) {
                throw new RuntimeException("Pengambilan Cuti minimal 3 hari");
            }
            // If the total days of leave is less than the total remaining quota of the year
            else if (totalHariKerja < totalSisaKuota) {
                throw new RuntimeException("Sisa Kuota Cuti " + totalSisaKuota + " hari harus diambil semua");
            }
        }
    }

    /**
     * Validates a leave claim request.
     * This method checks if the employee has taken a leave, if the employee has
     * any pending claim request, if the employee has any pending/returned cuti
     * besar, and if the employee has any leave request that is not approved.
     *
     * @param request the leave claim request
     * @return the leave that is claimed
     * @throws RuntimeException if the employee has any pending claim request or
     *                          if the employee has any pending/returned cuti
     *                          besar, or if the employee has any leave request
     *                          that is not approved.
     */
    public CutiPegawai validateKlaim(CutiPengajuanKlaimPostRequest request) {
        // cek apakah referensi cuti ini sudah disetujui
        CutiPegawai cutiPegawai = repository.findByIdAndApprovalCutiStatus(
                request.getRefCutiId(), EApprovalCutiStatus.APPROVED
        ).orElseThrow(() -> new RuntimeException("Unknown Cuti Pegawai"));

        if (List.of(defConfig.getJenisCutiTahunan(), defConfig.getJenisCutiIbadah()).contains(cutiPegawai.getJenisCuti().getId()))
            throw new RuntimeException("Cuti ini tidak perlu di klaim");

        // cek apakah pengajuan klaim cuti ini sudah ada
        boolean exists = repository.exists(request.getSpecification());
        if (exists) {
            throw new RuntimeException("Pengajuan Klaim Cuti ini sudah ada");
        }

        // cek apakah ada cuti melaksanakan ibadah yang masih berlangsung atau belum disetujui
        boolean existCutiIbadah = repository.existsByPegawai_IdAndJenisCuti_IdAndApprovalCutiStatusIn(
                request.getPegawaiId(),
                defConfig.getJenisCutiIbadah(),
                List.of(EApprovalCutiStatus.PENDING, EApprovalCutiStatus.RETURNED)
        );
        if (existCutiIbadah) {
            throw new RuntimeException("Klaim cuti tidak dapat diproses karena masih ada pengajuan cuti melaksanakan ibadah yang masih berlangsung");
        }

        return cutiPegawai;
    }
}
