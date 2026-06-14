package id.perumdamts.kepegawaian.services.master.jenisSp;

import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpPostRequest;
import id.perumdamts.kepegawaian.entities.master.JenisSp;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.master.jenisSp.JenisSpMapper;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenisSpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JenisSpCommandService {
    private final JenisSpRepository repository;

    @Transactional
    public JenisSp create(JenisSpPostRequest request) {
        Optional<JenisSp> existing = repository.findOne(request.getSpecification());
        if (existing.isPresent()) {
            if (existing.get().getIsDeleted()) {
                JenisSp revived = existing.get();
                revived.setIsDeleted(false);
                JenisSpMapper.updateEntity(revived, request);
                return repository.save(revived);
            }
            throw new ConflictException("Jenis SP already exists");
        }
        return repository.save(JenisSpMapper.toEntity(request));
    }

    @Transactional
    public JenisSp update(Long id, JenisSpPostRequest request) {
        JenisSp existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Jenis SP not found"));
        Optional<JenisSp> duplicate = repository.findOne(request.getSpecification());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new ConflictException("Jenis SP already exists");
        }
        JenisSpMapper.updateEntity(existing, request);
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        JenisSp existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Jenis SP not found"));
        existing.setIsDeleted(true);
        repository.save(existing);
    }
}
