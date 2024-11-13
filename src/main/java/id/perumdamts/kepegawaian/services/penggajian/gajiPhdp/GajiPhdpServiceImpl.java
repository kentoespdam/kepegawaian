package id.perumdamts.kepegawaian.services.penggajian.gajiPhdp;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpResponse;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPhdp;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiPhdpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiPhdpServiceImpl implements GajiPhdpService {
    private final GajiPhdpRepository repository;

    @Override
    public Page<GajiPhdpResponse> findPage(GajiPhdpRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(GajiPhdpResponse::from);
    }

    @Override
    public List<GajiPhdpResponse> findAll() {
        return repository.findAll()
                .stream().map(GajiPhdpResponse::from).toList();
    }

    @Override
    public GajiPhdpResponse findById(Long id) {
        return repository.findById(id).map(GajiPhdpResponse::from)
                .orElse(null);
    }

    @Override
    public SavedStatus<?> save(GajiPhdpPostRequest request) {
        Optional<GajiPhdp> one = repository.findOne(request.getSpecification());
        if (one.isPresent())
            return SavedStatus.build(ESaveStatus.DUPLICATE, "PhDP sudah ada");
        GajiPhdp entity = GajiPhdpPostRequest.toEntity(request);
        GajiPhdp save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public SavedStatus<?> update(Long id, GajiPhdpPutRequest request) {
        Optional<GajiPhdp> byId = repository.findById(id);
        if (byId.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown PhDP");
        GajiPhdp entity = GajiPhdpPutRequest.toEntity(byId.get(), request);
        GajiPhdp save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public Boolean delete(Long id) {
        Optional<GajiPhdp> byId = repository.findById(id);
        if (byId.isEmpty()) return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }
}
