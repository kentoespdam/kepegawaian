package id.perumdamts.kepegawaian.services.master.jenisSk;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.jenisSk.JenisSkPostRequest;
import id.perumdamts.kepegawaian.dto.master.jenisSk.JenisSkPutRequest;
import id.perumdamts.kepegawaian.dto.master.jenisSk.JenisSkRequest;
import id.perumdamts.kepegawaian.dto.master.jenisSk.JenisSkResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JenisSkService {
    List<JenisSkResponse> findAll(JenisSkRequest request);

    Page<JenisSkResponse> findPage(JenisSkRequest request);

    JenisSkResponse findById(Long id);

    SavedStatus<?> save(JenisSkPostRequest request);

    SavedStatus<?> saveBatch(List<JenisSkPostRequest> requests);

    SavedStatus<?> update(Long id, JenisSkPutRequest request);

    boolean deleteById(Long id);
}
