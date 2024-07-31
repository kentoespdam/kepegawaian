package id.perumdamts.kepegawaian.services.master.jenisSp;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpPostRequest;
import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpPutRequest;
import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpRequest;
import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JenisSpService {
    List<JenisSpResponse> findAll(JenisSpRequest request);

    Page<JenisSpResponse> findPage(JenisSpRequest request);

    JenisSpResponse findById(Long id);

    SavedStatus<?> save(JenisSpPostRequest request);

    SavedStatus<?> saveBatch(List<JenisSpPostRequest> requests);

    SavedStatus<?> update(Long id, JenisSpPutRequest request);

    boolean deleteById(Long id);
}
