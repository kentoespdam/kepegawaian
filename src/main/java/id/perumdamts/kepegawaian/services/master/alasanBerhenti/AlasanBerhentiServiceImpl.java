package id.perumdamts.kepegawaian.services.master.alasanBerhenti;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiPostRequest;
import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiPutRequest;
import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiRequest;
import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiResponse;
import id.perumdamts.kepegawaian.entities.master.AlasanBerhenti;
import id.perumdamts.kepegawaian.repositories.master.AlasanBerhentiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlasanBerhentiServiceImpl implements AlasanBerhentiService {
    private final AlasanBerhentiRepository repository;

    @Override
    public List<AlasanBerhentiResponse> findAll() {
        return repository.findAll().stream().map(AlasanBerhentiResponse::from).toList();
    }

    @Override
    public Page<AlasanBerhentiResponse> findPage(AlasanBerhentiRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(AlasanBerhentiResponse::from);
    }

    @Override
    public AlasanBerhentiResponse findById(Long id) {
        return repository.findById(id).map(AlasanBerhentiResponse::from).orElse(null);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(AlasanBerhentiPostRequest request) {
        try {
            boolean exists = repository.exists(request.getSpecification());
            if (exists)
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Alasan Berhenti sudah ada");
            AlasanBerhenti entity = AlasanBerhentiPostRequest.toEntity(request);
            AlasanBerhenti save = repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> saveBatch(List<AlasanBerhentiPostRequest> requests) {
        requests.stream().map(AlasanBerhentiPostRequest::toEntity).forEach(repository::save);
        return SavedStatus.build(ESaveStatus.SUCCESS, null);
    }

    @Override
    public SavedStatus<?> update(Long id, AlasanBerhentiPutRequest request) {
        Optional<AlasanBerhenti> byId = repository.findById(id);
        if (byId.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Alasan Berhenti not found");
        AlasanBerhenti entity = AlasanBerhentiPutRequest.toEntity(byId.get(), request);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, null);
    }

    @Override
    public boolean deleteById(Long id) {
        Optional<AlasanBerhenti> alasanBerhenti = repository.findById(id);
        if (alasanBerhenti.isEmpty())
            return false;
        alasanBerhenti.get().setIsDeleted(true);
        repository.save(alasanBerhenti.get());
        return true;
    }
}
