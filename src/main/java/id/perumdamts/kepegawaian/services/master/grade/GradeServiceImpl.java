package id.perumdamts.kepegawaian.services.master.grade;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.grade.GradePostRequest;
import id.perumdamts.kepegawaian.dto.master.grade.GradeRequest;
import id.perumdamts.kepegawaian.dto.master.grade.GradeResponse;
import id.perumdamts.kepegawaian.entities.master.Grade;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.repositories.master.GradeRepository;
import id.perumdamts.kepegawaian.repositories.master.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {
    private final GradeRepository repository;
    private final LevelRepository levelRepository;

    @Override
    public List<GradeResponse> findAll() {
        return repository.findAll().stream().map(GradeResponse::from).toList();
    }

    @Override
    public Page<GradeResponse> findPage(GradeRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(GradeResponse::from);
    }

    @Override
    public GradeResponse findById(Long id) {
        return repository.findById(id).map(GradeResponse::from).orElse(null);
    }

    @Override
    public SavedStatus<?> save(GradePostRequest request) {
        Optional<Level> level = levelRepository.findById(request.getLevelId());
        if (level.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Level");
        Optional<Grade> cari = repository.findByLevel_IdAndGradeAndTukin(
                request.getLevelId(),
                request.getGrade(),
                request.getTukin());
        if (cari.isPresent())
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Grade sudah ada");

        Grade entity = GradePostRequest.toEntity(request);
        Grade save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public SavedStatus<?> saveBatch(List<GradePostRequest> requests) {
        List<Grade> entities = GradePostRequest.toEntities(requests);
        repository.saveAll(entities);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Success Saving Batch Data");
    }

    @Override
    public SavedStatus<?> update(Long id, GradePostRequest request) {
        Optional<Level> level = levelRepository.findById(request.getLevelId());
        if (level.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Level");
        Optional<Grade> byId = repository.findById(id);
        if (byId.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Grade");
        Grade entity = GradePostRequest.toEntity(request, id);
        Grade save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public Boolean deleteById(Long id) {
        Optional<Grade> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        repository.deleteById(id);
        return true;
    }
}
