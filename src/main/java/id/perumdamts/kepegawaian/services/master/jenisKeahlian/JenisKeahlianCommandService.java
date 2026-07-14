package id.perumdamts.kepegawaian.services.master.jenisKeahlian;

import id.perumdamts.kepegawaian.dto.master.jenisKeahlian.JenisKeahlianPostRequest;
import id.perumdamts.kepegawaian.entities.master.JenisKeahlian;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.master.jenisKeahlian.JenisKeahlianMapper;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenisKeahlianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JenisKeahlianCommandService {
    private final JenisKeahlianRepository repository;

    @Transactional
    public JenisKeahlian create(JenisKeahlianPostRequest request) {
        Optional<JenisKeahlian> existing = repository.findOne(request.getSpecification());
        if (existing.isPresent()) {
            if (existing.get().getIsDeleted()) {
                JenisKeahlian revived = existing.get();
                revived.setIsDeleted(false);
                return repository.save(revived);
            } else {
                throw new ConflictException("JenisKeahlian already exists");
            }
        }
        JenisKeahlian entity = JenisKeahlianMapper.toEntity(request);
        return repository.save(entity);
    }

    @Transactional
    public JenisKeahlian update(Long id, JenisKeahlianPostRequest request) {
        JenisKeahlian existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("JenisKeahlian not found"));

        Optional<JenisKeahlian> duplicate = repository.findOne(request.getSpecification());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new ConflictException("JenisKeahlian with same nama already exists");
        }

        JenisKeahlianMapper.updateEntity(existing, request);
        return repository.save(existing);
    }

    @Transactional
    public boolean delete(Long id) {
        JenisKeahlian existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("JenisKeahlian not found"));
        existing.setIsDeleted(true);
        repository.save(existing);
        return true;
    }
}
