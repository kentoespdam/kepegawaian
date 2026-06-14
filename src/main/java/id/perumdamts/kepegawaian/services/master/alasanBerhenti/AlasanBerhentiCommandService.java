package id.perumdamts.kepegawaian.services.master.alasanBerhenti;

import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiPostRequest;
import id.perumdamts.kepegawaian.entities.master.AlasanBerhenti;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.AlasanBerhentiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlasanBerhentiCommandService {
    private final AlasanBerhentiRepository repository;

    @Transactional
    public AlasanBerhenti create(AlasanBerhentiPostRequest request) {
        Optional<AlasanBerhenti> existing = repository.findOne(request.getSpecification());
        if (existing.isPresent()) {
            if (existing.get().getIsDeleted()) {
                AlasanBerhenti revived = existing.get();
                revived.setIsDeleted(false);
                return repository.save(revived);
            } else {
                throw new ConflictException("AlasanBerhenti already exists");
            }
        }
        AlasanBerhenti entity = AlasanBerhentiMapper.toEntity(request);
        return repository.save(entity);
    }

    @Transactional
    public AlasanBerhenti update(Long id, AlasanBerhentiPostRequest request) {
        AlasanBerhenti existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("AlasanBerhenti not found"));

        Optional<AlasanBerhenti> duplicate = repository.findOne(request.getSpecification());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new ConflictException("AlasanBerhenti with same nama already exists");
        }

        AlasanBerhentiMapper.updateEntity(existing, request);
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        AlasanBerhenti existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("AlasanBerhenti not found"));
        existing.setIsDeleted(true);
        repository.save(existing);
    }
}