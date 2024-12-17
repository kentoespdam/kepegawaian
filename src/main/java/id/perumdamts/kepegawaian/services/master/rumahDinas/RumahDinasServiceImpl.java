package id.perumdamts.kepegawaian.services.master.rumahDinas;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasPostRequest;
import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasPutRequest;
import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasRequest;
import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasResponse;
import id.perumdamts.kepegawaian.entities.master.RumahDinas;
import id.perumdamts.kepegawaian.repositories.master.RumahDinasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RumahDinasServiceImpl implements RumahDinasService {
    private final RumahDinasRepository repository;

    @Override
    public Page<RumahDinasResponse> findPage(RumahDinasRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(RumahDinasResponse::from);
    }

    @Override
    public List<RumahDinasResponse> findAll(RumahDinasRequest request) {
        return repository.findAll(request.getSpecification())
                .stream().map(RumahDinasResponse::from)
                .toList();
    }

    @Override
    public RumahDinasResponse findById(Long id) {
        return repository.findById(id).map(RumahDinasResponse::from).orElse(null);
    }

    @Override
    public SavedStatus<?> save(RumahDinasPostRequest request) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists) return SavedStatus.build(ESaveStatus.DUPLICATE, "Rumah Dinas sudah ada");
        RumahDinas entity = RumahDinasPostRequest.toEntity(request);
        RumahDinas save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public SavedStatus<?> update(Long id, RumahDinasPutRequest request) {
        Optional<RumahDinas> byId = repository.findById(id);
        if (byId.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Rumah Dinas");
        RumahDinas entity = RumahDinasPutRequest.toEntity(byId.get(), request);
        RumahDinas save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public Boolean delete(Long id) {
        Optional<RumahDinas> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }
}
