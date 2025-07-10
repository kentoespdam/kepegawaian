package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.*;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface CutiPengajuanService {
    Page<CutiPengajuanResponse> findPage(CutiPengajuanRequest request);

    CutiPengajuanResponse findById(Long id);

    SavedStatus<?> save(CutiPengajuanPostRequest request);

    SavedStatus<?> update(Long id, CutiPengajuanPutRequest request);

    SavedStatus<?> klaim(CutiPengajuanKlaimPostRequest request);

    SavedStatus<?> updateKlaim(Long id, CutiPengajuanKlaimPostRequest request);

    SavedStatus<?> pembatalan(Long id);

    Integer findTotalHariKerja(LocalDate tanggalMulai, LocalDate tanggalSelesai);

    boolean delete(Long id);
}
