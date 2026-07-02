package id.perumdamts.kepegawaian.services.penggajian.dasarGaji;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiPutRequest;
import id.perumdamts.kepegawaian.entities.penggajian.DasarGaji;
import id.perumdamts.kepegawaian.mapper.penggajian.dasarGaji.DasarGajiMapper;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.DasarGajiRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DasarGajiCommandService {
    private final DasarGajiRepository repository;

    @Transactional
    public SavedStatus<?> save(DasarGajiPostRequest request) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists)
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Dasar Gaji sudah ada");
        DasarGaji entity = DasarGajiMapper.toEntity(request);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Dasar Gaji Saved");
    }

    @Transactional
    public SavedStatus<?> saveBatch(List<DasarGajiPostRequest> requests) {
        try {
            requests.stream().map(DasarGajiMapper::toEntity).forEach(repository::save);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Save Batch Dasar Gaji Success");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    public SavedStatus<?> update(Long id, DasarGajiPutRequest request) {
        Optional<DasarGaji> byId = repository.findById(id);
        if (byId.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Dasar Gaji not found");
        DasarGaji entity = byId.get();
        DasarGajiMapper.updateEntity(entity, request);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Dasar Gaji Updated");
    }

    @Transactional
    public boolean deleteById(Long id) {
        boolean exists = repository.existsById(id);
        if (!exists)
            return false;
        repository.deleteById(id);
        return true;
    }
}
