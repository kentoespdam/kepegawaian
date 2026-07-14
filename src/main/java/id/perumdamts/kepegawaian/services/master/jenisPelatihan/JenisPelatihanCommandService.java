package id.perumdamts.kepegawaian.services.master.jenisPelatihan;

import id.perumdamts.kepegawaian.dto.master.jenisPelatihan.JenisPelatihanPostRequest;
import id.perumdamts.kepegawaian.entities.master.JenisPelatihan;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.master.jenisPelatihan.JenisPelatihanMapper;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenisPelatihanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JenisPelatihanCommandService {
    private final JenisPelatihanRepository repository;

    @Transactional
    public JenisPelatihan create(JenisPelatihanPostRequest request) {
        Optional<JenisPelatihan> existing = repository.findOne(request.getSpecification());
        if (existing.isPresent()) {
            if (existing.get().getIsDeleted()) {
                JenisPelatihan revived = existing.get();
                revived.setIsDeleted(false);
                return repository.save(revived);
            } else {
                throw new ConflictException("JenisPelatihan already exists");
            }
        }
        JenisPelatihan entity = JenisPelatihanMapper.toEntity(request);
        return repository.save(entity);
    }

    @Transactional
    public JenisPelatihan update(Long id, JenisPelatihanPostRequest request) {
        JenisPelatihan existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("JenisPelatihan not found"));

        Optional<JenisPelatihan> duplicate = repository.findOne(request.getSpecification());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new ConflictException("JenisPelatihan with same nama already exists");
        }

        JenisPelatihanMapper.updateEntity(existing, request);
        return repository.save(existing);
    }

    @Transactional
    public boolean delete(Long id) {
        JenisPelatihan existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("JenisPelatihan not found"));
        existing.setIsDeleted(true);
        repository.save(existing);
        return true;
    }
}
