package id.perumdamts.kepegawaian.services.penggajian.gajiPhdp;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpPutRequest;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPhdp;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
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
    public SavedStatus<Long> save(GajiPhdpPostRequest request) {
        Optional<GajiPhdp> one = repository.findOne(request.getSpecification());
        if (one.isPresent())
            throw new ConflictException("PhDP sudah ada");
        GajiPhdp entity = GajiPhdpMapper.toEntity(request);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional
    public SavedStatus<Long> update(Long id, GajiPhdpPutRequest request) {
        GajiPhdp entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("PhDP not found"));
        GajiPhdpMapper.updateEntity(entity, request);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
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
