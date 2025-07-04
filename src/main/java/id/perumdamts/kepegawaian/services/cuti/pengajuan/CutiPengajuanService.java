package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanRequest;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface CutiPengajuanService {
    Page<CutiPengajuanResponse> findPage(CutiPengajuanRequest request);

    CutiPengajuanResponse findById(Long id);

    SavedStatus<?> save(CutiPengajuanPostRequest request);

    SavedStatus<?> pembatalan(Long id);

    Integer findTotalHariKerja(LocalDate tanggalMulai, LocalDate tanggalSelesai);

    boolean delete(Long id);
}
