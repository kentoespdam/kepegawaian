package id.perumdamts.kepegawaian.services.master.level;

import id.perumdamts.kepegawaian.dto.master.level.LevelPostRequest;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.master.level.LevelMapper;
import id.perumdamts.kepegawaian.repositories.master.jpa.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LevelCommandService {
    private final LevelRepository repository;

    @Transactional
    public Level create(LevelPostRequest request) {
        Optional<Level> existing = repository.findOne(request.getSpecification());
        if (existing.isPresent()) {
            if (existing.get().getIsDeleted()) {
                Level revived = existing.get();
                revived.setIsDeleted(false);
                return repository.save(revived);
            } else {
                throw new ConflictException("Level already exists");
            }
        }

        Level entity = LevelMapper.toEntity(request);
        return repository.save(entity);
    }

    @Transactional
    public Level update(Long id, LevelPostRequest request) {
        Level existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Level not found"));

        Optional<Level> dup = repository.findOne(request.getSpecification());
        if (dup.isPresent() && !dup.get().getId().equals(id)) {
            throw new ConflictException("Level already exists");
        }

        LevelMapper.updateEntity(existing, request);
        return repository.save(existing);
    }

    @Transactional
    public boolean delete(Long id) {
        Level existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Level not found"));
        existing.setIsDeleted(true);
        repository.save(existing);
        return true;
    }

    @Transactional
    public List<Level> createBatch(List<LevelPostRequest> requests) {
        List<Level> entities = requests.stream().map(req -> LevelMapper.toEntity(req)).toList();
        return repository.saveAll(entities);
    }
}
