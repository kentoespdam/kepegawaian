package id.perumdamts.kepegawaian.services.master.hariLibur;

import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburPostRequest;
import id.perumdamts.kepegawaian.entities.master.HariLibur;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.master.hariLibur.HariLiburMapper;
import id.perumdamts.kepegawaian.repositories.master.jpa.HariLiburRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HariLiburCommandService {
    private final HariLiburRepository repository;

    @Transactional
    public HariLibur create(HariLiburPostRequest request) {
        Optional<HariLibur> existing = repository.findOne(request.getSpecification());
        if (existing.isPresent()) {
            if (existing.get().getIsDeleted()) {
                HariLibur revived = existing.get();
                revived.setIsDeleted(false);
                return repository.save(revived);
            } else {
                throw new ConflictException("HariLibur already exists");
            }
        }
        HariLibur entity = HariLiburMapper.toEntity(request);
        return repository.save(entity);
    }

    @Transactional
    public HariLibur update(Long id, HariLiburPostRequest request) {
        HariLibur existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("HariLibur not found"));

        Optional<HariLibur> duplicate = repository.findOne(request.getSpecification());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new ConflictException("HariLibur with same tanggal and jenisLibur already exists");
        }

        HariLiburMapper.updateEntity(existing, request);
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        HariLibur existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("HariLibur not found"));
        existing.setIsDeleted(true);
        repository.save(existing);
    }
}
