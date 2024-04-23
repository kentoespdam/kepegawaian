package id.perumdamts.kepegawaian.services.profil.pendidikan;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PendidikanService {
    List<PendidikanResponse> findAll();
    Page<PendidikanResponse> findPage(PendidikanRequest request);
    PendidikanResponse findById(Long id);
    List<PendidikanResponse> findByBiodataId(String biodataId);
    SavedStatus<?> save(PendidikanPostRequest request);
    SavedStatus<?> update(Long id, PendidikanPutRequest request);
    SavedStatus<?> acceptPendidikan(Long id, PendidikanAcceptRequest request, String username);
    Boolean delete(Long id);
}
