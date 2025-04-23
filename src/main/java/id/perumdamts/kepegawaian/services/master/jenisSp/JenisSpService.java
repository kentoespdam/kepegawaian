package id.perumdamts.kepegawaian.services.master.jenisSp;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.jenisSp.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JenisSpService {
    Page<JenisSpResponse> findPage(JenisSpRequest request);

    List<JenisSpMiniResponse> findList();

    JenisSpResponse findById(Long id);

    SavedStatus<?> save(JenisSpPostRequest request);

    SavedStatus<?> update(Long id, JenisSpPutRequest request);

    boolean delete(Long id);
}
