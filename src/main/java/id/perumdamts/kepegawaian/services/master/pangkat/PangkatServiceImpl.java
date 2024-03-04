package id.perumdamts.kepegawaian.services.master.pangkat;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.pangkat.PangkatPostRequest;
import id.perumdamts.kepegawaian.dto.master.pangkat.PangkatRequest;
import id.perumdamts.kepegawaian.dto.master.pangkat.PangkatResponse;
import id.perumdamts.kepegawaian.entities.master.Pangkat;
import id.perumdamts.kepegawaian.repositories.master.PangkatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PangkatServiceImpl implements PangkatService {
    private final PangkatRepository repository;

    @Override
    public List<PangkatResponse> findAll() {
        return repository.findAll().stream().map(PangkatResponse::from).toList();
    }

    @Override
    public Page<PangkatResponse> findPage(PangkatRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(PangkatResponse::from);
    }

    @Override
    public PangkatResponse findById(Long id) {
        return repository.findById(id).map(PangkatResponse::from).orElse(null);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(PangkatPostRequest request) {
        Optional<Pangkat> cari = repository.findByNama(request.getNama());
        if (cari.isPresent())
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Pangkat sudah ada");
        Pangkat entity = PangkatPostRequest.toEntity(request);
        Pangkat save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public SavedStatus<?> saveBatch(List<PangkatPostRequest> requests) {
        List<Pangkat> entities = PangkatPostRequest.toEntities(requests);
        repository.saveAll(entities);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Success Saving Batch Data");
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, PangkatPostRequest request) {
        Optional<Pangkat> byId = repository.findById(id);
        if (byId.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Pangkat");
        Pangkat entity = PangkatPostRequest.toEntity(request, id);
        Pangkat save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public Boolean deleteById(Long id) {
        Optional<Pangkat> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        repository.deleteById(id);
        return true;
    }
}
