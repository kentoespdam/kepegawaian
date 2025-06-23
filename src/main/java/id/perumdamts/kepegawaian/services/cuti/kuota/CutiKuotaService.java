package id.perumdamts.kepegawaian.services.cuti.kuota;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.kuota.*;
import org.springframework.data.domain.Page;

public interface CutiKuotaService {
    Page<CutiKuotaPegawaiResponse> findPage(CutiKuotaRequest request);
    CutiKuotaDetailResponse findById(Long id);
    SavedStatus<?> save(CutiKuotaPostRequest request);
    SavedStatus<?> update(Long id, CutiKuotaPutRequest request);
    SavedStatus<?> importData(CutiKuotaImportRequest request);
    boolean delete(Long id);
}
