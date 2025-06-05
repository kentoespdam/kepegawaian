package id.perumdamts.kepegawaian.services.cuti.kuota;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.kuota.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CutiKuotaService {
    Page<CutiKuotaResponse> findPage(CutiKuotaRequest request);
    CutiKuotaResponse findById(Long id);
    List<CutiKuotaResponse> findByPegawai(Long pegawaiId);
    SavedStatus<?> save(CutiKuotaPostRequest request);
    SavedStatus<?> update(Long id, CutiKuotaPutRequest request);
    SavedStatus<?> importData(CutiKuotaImportRequest request);
    boolean delete(Long id);
}
