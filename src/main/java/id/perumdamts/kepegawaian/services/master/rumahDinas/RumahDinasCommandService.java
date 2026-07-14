package id.perumdamts.kepegawaian.services.master.rumahDinas;

import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasPostRequest;
import id.perumdamts.kepegawaian.entities.master.RumahDinas;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.master.rumahDinas.RumahDinasMapper;
import id.perumdamts.kepegawaian.repositories.master.jpa.RumahDinasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RumahDinasCommandService {
    private final RumahDinasRepository repository;

    @Transactional
    public RumahDinas create(RumahDinasPostRequest request) {
        Optional<RumahDinas> existing = repository.findOne(request.getSpecification());
        if (existing.isPresent()) {
            if (existing.get().getIsDeleted()) {
                RumahDinas revived = existing.get();
                revived.setIsDeleted(false);
                RumahDinasMapper.updateEntity(revived, request);
                return repository.save(revived);
            } else {
                throw new ConflictException("RumahDinas already exists");
            }
        }
        RumahDinas entity = RumahDinasMapper.toEntity(request);
        return repository.save(entity);
    }

    @Transactional
    public RumahDinas update(Long id, RumahDinasPostRequest request) {
        RumahDinas existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("RumahDinas not found"));

        Optional<RumahDinas> duplicate = repository.findOne(request.getSpecification());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new ConflictException("RumahDinas with same nama already exists");
        }

        RumahDinasMapper.updateEntity(existing, request);
        return repository.save(existing);
    }

    @Transactional
    public boolean delete(Long id) {
        RumahDinas existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("RumahDinas not found"));
        existing.setIsDeleted(true);
        repository.save(existing);
        return true;
    }
}
