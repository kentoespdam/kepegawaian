package id.perumdamts.kepegawaian.services.penggajian.dasarGaji;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiPutRequest;
import id.perumdamts.kepegawaian.entities.penggajian.DasarGaji;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.penggajian.dasarGaji.DasarGajiMapper;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.DasarGajiRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DasarGajiCommandService {
    private final DasarGajiRepository repository;

    @Transactional
    public SavedStatus<Long> save(DasarGajiPostRequest request) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists)
            throw new ConflictException("Dasar Gaji sudah ada");
        DasarGaji entity = DasarGajiMapper.toEntity(request);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional
    public SavedStatus<String> saveBatch(List<DasarGajiPostRequest> requests) {
        requests.stream().map(DasarGajiMapper::toEntity).forEach(repository::save);
        return SavedStatus.build(ESaveStatus.SUCCESS, requests.size() + " success");
    }

    @Transactional
    public SavedStatus<Long> update(Long id, DasarGajiPutRequest request) {
        DasarGaji entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dasar Gaji not found"));
        DasarGajiMapper.updateEntity(entity, request);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
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
