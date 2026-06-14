package id.perumdamts.kepegawaian.services.master.grade;

import id.perumdamts.kepegawaian.dto.master.grade.GradePostRequest;
import id.perumdamts.kepegawaian.entities.master.Grade;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.master.grade.GradeMapper;
import id.perumdamts.kepegawaian.repositories.master.jpa.GradeRepository;
import id.perumdamts.kepegawaian.repositories.master.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GradeCommandService {
    private final GradeRepository repository;
    private final LevelRepository levelRepository;

    @Transactional
    public Grade create(GradePostRequest request) {
        Level level = findLevel(request.getLevelId());
        Optional<Grade> existing = repository.findOne(request.getSpecification());
        if (existing.isPresent()) {
            if (existing.get().getIsDeleted()) {
                Grade revived = existing.get();
                revived.setIsDeleted(false);
                GradeMapper.updateEntity(revived, request, level);
                return repository.save(revived);
            }
            throw new ConflictException("Grade already exists");
        }
        Grade entity = GradeMapper.toEntity(request, level);
        return repository.save(entity);
    }

    @Transactional
    public Grade update(Long id, GradePostRequest request) {
        Grade existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Grade not found"));
        Level level = findLevel(request.getLevelId());
        Optional<Grade> duplicate = repository.findOne(request.getSpecification());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new ConflictException("Grade already exists");
        }
        GradeMapper.updateEntity(existing, request, level);
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Grade existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Grade not found"));
        existing.setIsDeleted(true);
        repository.save(existing);
    }

    private Level findLevel(Long id) {
        if (id == null) return null;
        return levelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Level not found"));
    }
}
