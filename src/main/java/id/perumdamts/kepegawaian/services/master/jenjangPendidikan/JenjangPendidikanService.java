package id.perumdamts.kepegawaian.services.master.jenjangPendidikan;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanPostRequest;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanPutRequest;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanRequest;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JenjangPendidikanService {
    List<JenjangPendidikanResponse> findAll();
    Page<JenjangPendidikanResponse> findPage(JenjangPendidikanRequest request);
    JenjangPendidikanResponse findById(Long id);
    SavedStatus<?> save(JenjangPendidikanPostRequest request);
    SavedStatus<?> saveBatch(List<JenjangPendidikanPostRequest> requests);
    SavedStatus<?> update(Long id, JenjangPendidikanPutRequest request);
    Boolean deleteById(Long id);
}
