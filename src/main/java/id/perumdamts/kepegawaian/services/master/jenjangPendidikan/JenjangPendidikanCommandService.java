package id.perumdamts.kepegawaian.services.master.jenjangPendidikan;

import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanPostRequest;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanPutRequest;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.master.jenjangPendidikan.JenjangPendidikanMapper;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenjangPendidikanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JenjangPendidikanCommandService {
    private final JenjangPendidikanRepository repository;

    @Transactional
    public JenjangPendidikan create(JenjangPendidikanPostRequest request) {
        Optional<JenjangPendidikan> existing = repository.findOne(request.getSpecification());
        if (existing.isPresent()) {
            if (existing.get().getIsDeleted()) {
                JenjangPendidikan revived = existing.get();
                revived.setIsDeleted(false);
                return repository.save(revived);
            } else {
                throw new ConflictException("JenjangPendidikan already exists");
            }
        }
        JenjangPendidikan entity = JenjangPendidikanMapper.toEntity(request);
        return repository.save(entity);
    }

    @Transactional
    public List<JenjangPendidikan> saveBatch(List<JenjangPendidikanPostRequest> requests) {
        List<JenjangPendidikan> entities = requests.stream().map(JenjangPendidikanMapper::toEntity).toList();
        return repository.saveAll(entities);
    }

    @Transactional
    public JenjangPendidikan update(Long id, JenjangPendidikanPutRequest request) {
        JenjangPendidikan existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("JenjangPendidikan not found"));

        Optional<JenjangPendidikan> duplicate = repository.findOne(request.getSpecification());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new ConflictException("JenjangPendidikan with same nama already exists");
        }

        JenjangPendidikanMapper.updateEntity(existing, request);
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        JenjangPendidikan existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("JenjangPendidikan not found"));
        existing.setIsDeleted(true);
        repository.save(existing);
    }
}
