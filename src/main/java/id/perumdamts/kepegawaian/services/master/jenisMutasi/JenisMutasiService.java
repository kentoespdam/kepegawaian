package id.perumdamts.kepegawaian.services.master.jenisMutasi;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.jenisMutasi.JenisMutasiPostRequest;
import id.perumdamts.kepegawaian.dto.master.jenisMutasi.JenisMutasiPutRequest;
import id.perumdamts.kepegawaian.dto.master.jenisMutasi.JenisMutasiRequest;
import id.perumdamts.kepegawaian.dto.master.jenisMutasi.JenisMutasiResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JenisMutasiService {
    List<JenisMutasiResponse> findAll(JenisMutasiRequest request);

    Page<JenisMutasiResponse> findPage(JenisMutasiRequest request);

    JenisMutasiResponse findById(Long id);

    SavedStatus<?> save(JenisMutasiPostRequest request);

    SavedStatus<?> saveBatch(List<JenisMutasiPostRequest> requests);

    SavedStatus<?> update(Long id, JenisMutasiPutRequest request);

    boolean deleteById(Long id);
}
