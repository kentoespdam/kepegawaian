package id.perumdamts.kepegawaian.services.master.jenisKitas;

import id.perumdamts.kepegawaian.dto.master.jenisKitas.JenisKitasPostRequest;
import id.perumdamts.kepegawaian.entities.master.JenisKitas;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.mapper.master.jenisKitas.JenisKitasMapper;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenisKitasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JenisKitasCommandService {
    private final JenisKitasRepository repository;

    @Transactional
    public JenisKitas create(JenisKitasPostRequest request) {
        Optional<JenisKitas> existing = repository.findOne(request.getSpecification());
        if (existing.isPresent()) {
            if (existing.get().getIsDeleted()) {
                JenisKitas revived = existing.get();
                revived.setIsDeleted(false);
                return repository.save(revived);
            } else {
                throw new ConflictException("JenisKitas already exists");
            }
        }
        JenisKitas entity = JenisKitasMapper.toEntity(request);
        return repository.save(entity);
    }

    @Transactional
    public JenisKitas update(Long id, JenisKitasPostRequest request) {
        JenisKitas existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("JenisKitas not found"));

        Optional<JenisKitas> duplicate = repository.findOne(request.getSpecification());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new ConflictException("JenisKitas with same nama already exists");
        }

        JenisKitasMapper.updateEntity(existing, request);
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        JenisKitas existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("JenisKitas not found"));
        existing.setIsDeleted(true);
        repository.save(existing);
    }
}
