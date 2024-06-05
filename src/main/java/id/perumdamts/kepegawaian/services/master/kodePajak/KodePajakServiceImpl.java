package id.perumdamts.kepegawaian.services.master.kodePajak;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.kodePajak.KodePajakPostRequest;
import id.perumdamts.kepegawaian.dto.master.kodePajak.KodePajakPutRequest;
import id.perumdamts.kepegawaian.dto.master.kodePajak.KodePajakRequest;
import id.perumdamts.kepegawaian.dto.master.kodePajak.KodePajakResponse;
import id.perumdamts.kepegawaian.entities.master.KodePajak;
import id.perumdamts.kepegawaian.repositories.master.KodePajakRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KodePajakServiceImpl implements KodePajakService {
    private final KodePajakRepository repository;

    @Override
    public List<KodePajakResponse> findAll() {
        return repository.findAll().stream().map(KodePajakResponse::from).toList();
    }

    @Override
    public Page<KodePajakResponse> findPage(KodePajakRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(KodePajakResponse::from);
    }

    @Override
    public KodePajakResponse findById(Long id) {
        return repository.findById(id).map(KodePajakResponse::from).orElse(null);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(KodePajakPostRequest request) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists) return SavedStatus.build(ESaveStatus.DUPLICATE, "Kode Pajak sudah ada");
        KodePajak entity = KodePajakPostRequest.toEntity(request);
        KodePajak save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public SavedStatus<?> saveBatch(List<KodePajakPostRequest> requests) {
        List<KodePajak> entities = KodePajakPostRequest.toEntities(requests);
        repository.saveAll(entities);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Success Saving Batch Data");
    }

    @Override
    public SavedStatus<?> update(Long id, KodePajakPutRequest request) {
        Optional<KodePajak> kodePajak = repository.findById(id);
        if (kodePajak.isEmpty()) return SavedStatus.build(ESaveStatus.FAILED, "Unknown Kode Pajak");
        KodePajak entity = KodePajakPutRequest.toEntity(kodePajak.get(), request);
        KodePajak save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public boolean deleteById(Long id) {
        boolean exists = repository.existsById(id);
        if (!exists) return false;
        repository.deleteById(id);
        return true;
    }
}
