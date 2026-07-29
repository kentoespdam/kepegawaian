package id.perumdamts.kepegawaian.services.penggajian.dasarGaji;

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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DasarGajiCommandService {
    private final DasarGajiRepository repository;

    @Transactional
    public DasarGaji create(DasarGajiPostRequest request) {
        Optional<DasarGaji> existing = repository.findOne(request.getSpecification());
        if (existing.isPresent()) {
            if (existing.get().getIsDeleted()) {
                DasarGaji revived = existing.get();
                revived.setIsDeleted(false);
                return repository.save(revived);
            } else {
                throw new ConflictException("Dasar Gaji sudah ada");
            }
        }
        DasarGaji entity = DasarGajiMapper.toEntity(request);
        return repository.save(entity);
    }

    @Transactional
    public List<DasarGaji> createBatch(List<DasarGajiPostRequest> requests) {
        List<DasarGaji> entities = requests.stream().map(DasarGajiMapper::toEntity).toList();
        return repository.saveAll(entities);
    }

    @Transactional
    public DasarGaji update(Long id, DasarGajiPutRequest request) {
        DasarGaji entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dasar Gaji not found"));
        DasarGajiMapper.updateEntity(entity, request);
        return repository.save(entity);
    }

    @Transactional
    public boolean delete(Long id) {
        DasarGaji entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dasar Gaji not found"));
        entity.setIsDeleted(true);
        repository.save(entity);
        return true;
    }
}
