package id.perumdamts.kepegawaian.services.master.golongan;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganPostRequest;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.GolonganRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GolonganCommandService {
    private final GolonganRepository repository;

    @Transactional
    public Golongan create(GolonganPostRequest request) {
        // Check for existing soft-deleted record with same golongan/pangkat
        Optional<Golongan> existing = repository.findOne(request.getSpecification());
        if (existing.isPresent()) {
            if (existing.get().getIsDeleted()) {
                // Revive existing record
                Golongan revived = existing.get();
                revived.setIsDeleted(false);
                return repository.save(revived);
            } else {
                throw new ConflictException("Golongan already exists");
            }
        }

        // Create new record
        Golongan entity = GolonganMapper.toEntity(request);
        return repository.save(entity);
    }

    @Transactional
    public Golongan update(Long id, GolonganPostRequest request) {
        Golongan existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Golongan not found"));

        // Check for duplicate with different ID
        Optional<Golongan> duplicate = repository.findOne(request.getSpecification());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new ConflictException("Golongan with same golongan/pangkat already exists");
        }

        GolonganMapper.updateEntity(existing, request);
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Golongan existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Golongan not found"));
        existing.setIsDeleted(true);
        repository.save(existing);
    }
}
