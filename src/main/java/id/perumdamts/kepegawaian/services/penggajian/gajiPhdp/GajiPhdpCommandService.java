package id.perumdamts.kepegawaian.services.penggajian.gajiPhdp;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpPutRequest;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPhdp;
import id.perumdamts.kepegawaian.mapper.penggajian.gajiPhdp.GajiPhdpMapper;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiPhdpRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiPhdpCommandService {
    private final GajiPhdpRepository repository;

    @Transactional
    public SavedStatus<?> save(GajiPhdpPostRequest request) {
        Optional<GajiPhdp> one = repository.findOne(request.getSpecification());
        if (one.isPresent())
            return SavedStatus.build(ESaveStatus.DUPLICATE, "PhDP sudah ada");
        GajiPhdp entity = GajiPhdpMapper.toEntity(request);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, "PhDP Saved");
    }

    @Transactional
    public SavedStatus<?> update(Long id, GajiPhdpPutRequest request) {
        Optional<GajiPhdp> byId = repository.findById(id);
        if (byId.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown PhDP");
        GajiPhdp entity = byId.get();
        GajiPhdpMapper.updateEntity(entity, request);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, "PhDP Updated");
    }

    @Transactional
    public Boolean delete(Long id) {
        Optional<GajiPhdp> byId = repository.findById(id);
        if (byId.isEmpty()) return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }
}
