package id.perumdamts.kepegawaian.services.master.level;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.level.LevelPostRequest;
import id.perumdamts.kepegawaian.dto.master.level.LevelRequest;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.repositories.master.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LevelServiceImpl implements LevelService {
    private final LevelRepository repository;

    @Override
    public List<LevelResponse> findAll() {
        return repository.findAll().stream().map(LevelResponse::from).toList();
    }

    @Override
    public Page<LevelResponse> findPage(LevelRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(LevelResponse::from);
    }

    @Override
    public LevelResponse findById(Long id) {
        return repository.findById(id).map(LevelResponse::from).orElse(null);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(LevelPostRequest request) {
        Optional<Level> cari = repository.findOne(request.getSpecification());
        if (cari.isPresent())
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Level sudah ada");
        Level entity = LevelPostRequest.toEntity(request);
        Level save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public SavedStatus<?> saveBatch(List<LevelPostRequest> requests) {
        List<Level> entities = LevelPostRequest.toEntities(requests);
        repository.saveAll(entities);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Success Saving Batch Data");
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, LevelPostRequest request) {
        Optional<Level> byId = repository.findById(id);
        if (byId.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Level");
        Level entity = LevelPostRequest.toEntity(request, id);
        Level save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public Boolean deleteById(Long id) {
        Optional<Level> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        repository.deleteById(id);
        return true;
    }
}
