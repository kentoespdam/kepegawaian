package id.perumdamts.kepegawaian.services.master.profesi;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiPostRequest;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiRequest;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiResponse;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.repositories.master.LevelRepository;
import id.perumdamts.kepegawaian.repositories.master.ProfesiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfesiServiceImpl implements ProfesiService {
    private final ProfesiRepository repository;
    private final LevelRepository levelRepository;

    @Override
    public List<ProfesiResponse> findAll() {
        return repository.findAll().stream().map(ProfesiResponse::from).toList();
    }

    @Override
    public Page<ProfesiResponse> findPage(ProfesiRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(ProfesiResponse::from);
    }

    @Override
    public ProfesiResponse findById(Long id) {
        return repository.findById(id).map(ProfesiResponse::from).orElse(null);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(ProfesiPostRequest request) {
        Optional<Level> level = levelRepository.findById(request.getLevelId());
        if (level.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Level");
        Optional<Profesi> cari = repository.findOne(request.getSpecification());
        if (cari.isPresent())
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Profesi sudah ada");

        Profesi entity = ProfesiPostRequest.toEntity(request);
        Profesi save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public SavedStatus<?> saveBatch(List<ProfesiPostRequest> requests) {
        List<Profesi> entities = ProfesiPostRequest.toEntities(requests);
        repository.saveAll(entities);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Success Saving Batch Data");
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, ProfesiPostRequest request) {
        Optional<Level> level = levelRepository.findById(request.getLevelId());
        if (level.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Level");
        Optional<Profesi> byId = repository.findById(id);
        if (byId.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Profesi");
        Profesi entity = ProfesiPostRequest.toEntity(request, id);
        Profesi save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public Boolean deleteById(Long id) {
        Optional<Profesi> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        repository.deleteById(id);
        return true;
    }
}
