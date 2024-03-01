package id.perumdamts.kepegawaian.services.master.grade;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.grade.GradePostRequest;
import id.perumdamts.kepegawaian.dto.master.grade.GradeRequest;
import id.perumdamts.kepegawaian.dto.master.grade.GradeResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GradeService {
    List<GradeResponse> findAll();
    Page<GradeResponse> findPage(GradeRequest request);
    GradeResponse findById(Long id);
    SavedStatus<?> save(GradePostRequest request);
    SavedStatus<?> saveBatch(List<GradePostRequest> requests);
    SavedStatus<?> update(Long id, GradePostRequest request);
    Boolean deleteById(Long id);
}
