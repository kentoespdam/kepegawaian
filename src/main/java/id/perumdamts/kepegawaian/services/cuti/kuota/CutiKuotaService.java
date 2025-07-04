package id.perumdamts.kepegawaian.services.cuti.kuota;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.kuota.*;

public interface CutiKuotaService {
    CutiKuotaPegawaiResponse findPage(CutiKuotaRequest request);
    CutiKuotaResponse findById(Long id);
    CutiKuotaSisa findByPegawai(Long pegawaiId, Integer tahun);
    SavedStatus<?> save(CutiKuotaPostRequest request);
    SavedStatus<?> update(Long id, CutiKuotaPutRequest request);
    SavedStatus<?> importData(CutiKuotaImportRequest request);

    boolean delete(Long id);
}
