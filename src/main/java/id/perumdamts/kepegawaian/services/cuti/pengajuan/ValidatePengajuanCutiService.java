package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.config.DefConfig;
import id.perumdamts.kepegawaian.repositories.cuti.CutiPegawaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidatePengajuanCutiService {
    private final CutiPegawaiRepository repository;
    public void validate(CutiPengajuanPostRequest request) {
        DefConfig defConfig = new DefConfig();
        boolean existsPendingPengajuan = repository.exists(request.getPendingStatusSpecification());
        if (existsPendingPengajuan) throw new RuntimeException("Masih ada pengajuan cuti yang belum diapprove");
        boolean existBesar = repository.exists(request.getSpecificationByJenisCuti(defConfig.getJenisCutiBesar()));
        if (existBesar) throw new RuntimeException("Anda tidak berhak cuti tahunan karena telah mengambil cuti besar");
        boolean existIbadah = repository.exists(request.getSpecificationByJenisCuti(defConfig.getJenisCutiIbadah()));
        if (existIbadah)
            throw new RuntimeException("Anda tidak berhak cuti tahunan karena telah mengambil cuti melaksanakan ibadah");
    }

    public void validateMinimalCuti(int totalHariKerja, int totalSisaKuota) {
        if (totalHariKerja < 3) {
            if (totalSisaKuota >= 3) {
                throw new RuntimeException("Pengambilan Cuti minimal 3 hari");
            } else if (totalHariKerja < totalSisaKuota) {
                throw new RuntimeException("Sisa Kuota Cuti " + totalSisaKuota + " hari harus diambil semua");
            }
        }
    }
}
