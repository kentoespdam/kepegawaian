package id.perumdamts.kepegawaian.services.master.level;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.level.LevelPostRequest;
import id.perumdamts.kepegawaian.dto.master.level.LevelRequest;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LevelService {
    List<LevelResponse> findAll();
    Page<LevelResponse> findPage(LevelRequest request);
    LevelResponse findById(Long id);
    SavedStatus<?> save(LevelPostRequest request);
    SavedStatus<?> saveBatch(List<LevelPostRequest> requests);
    SavedStatus<?> update(Long id, LevelPostRequest request);
    Boolean deleteById(Long id);
}
