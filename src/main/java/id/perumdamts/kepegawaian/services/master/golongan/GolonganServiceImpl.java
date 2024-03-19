package id.perumdamts.kepegawaian.services.master.golongan;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganPostRequest;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganRequest;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.repositories.master.GolonganRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GolonganServiceImpl implements GolonganService {
    private final GolonganRepository repository;

    @Override
    public List<GolonganResponse> findAll() {
        return repository.findAll().stream().map(GolonganResponse::from).toList();
    }

    @Override
    public Page<GolonganResponse> findPage(GolonganRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(GolonganResponse::from);
    }

    @Override
    public GolonganResponse findById(Long id) {
        return repository.findById(id).map(GolonganResponse::from).orElse(null);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(GolonganPostRequest request) {
        Optional<Golongan> cari = repository.findByGolonganAndPangkat(request.getGolongan(), request.getPangkat());
        if (cari.isPresent())
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Golongan sudah ada");
        Golongan entity = GolonganPostRequest.toEntity(request);
        Golongan save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public SavedStatus<?> saveBatch(List<GolonganPostRequest> requests) {
        List<Golongan> entities = GolonganPostRequest.toEntities(requests);
        repository.saveAll(entities);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Success Saving Batch Data");
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, GolonganPostRequest request) {
        Optional<Golongan> byId = repository.findById(id);
        if (byId.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Golongan");
        Golongan entity = GolonganPostRequest.toEntity(request, id);
        Golongan save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public Boolean deleteById(Long id) {
        Optional<Golongan> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        repository.deleteById(id);
        return true;
    }
}
