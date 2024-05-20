package id.perumdamts.kepegawaian.services.master.jenisPelatihan;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.jenisPelatihan.JenisPelatihanPostRequest;
import id.perumdamts.kepegawaian.dto.master.jenisPelatihan.JenisPelatihanPutRequest;
import id.perumdamts.kepegawaian.dto.master.jenisPelatihan.JenisPelatihanRequest;
import id.perumdamts.kepegawaian.dto.master.jenisPelatihan.JenisPelatihanResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JenisPelatihanService {
    List<JenisPelatihanResponse> findAll();
    Page<JenisPelatihanResponse> findPage(JenisPelatihanRequest request);
    JenisPelatihanResponse findById(Long id);
    SavedStatus<?> save(JenisPelatihanPostRequest request);
    SavedStatus<?> saveBatch(List<JenisPelatihanPostRequest> requests);
    SavedStatus<?> update(Long id, JenisPelatihanPutRequest request);
    Boolean deleteById(Long id);
}
