package id.perumdamts.kepegawaian.services.master.apd;

import id.perumdamts.kepegawaian.dto.master.apd.ApdPostRequest;
import id.perumdamts.kepegawaian.entities.master.Apd;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.master.apd.ApdMapper;
import id.perumdamts.kepegawaian.repositories.master.jpa.ApdRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.ProfesiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApdCommandService {
    private final ApdRepository repository;
    private final ProfesiRepository profesiRepository;

    @Transactional
    public Apd create(Long profesiId, ApdPostRequest request) {
        if (!profesiRepository.existsById(profesiId)) {
            throw new NotFoundException("Profesi not found");
        }
        Profesi profesi = profesiRepository.getReferenceById(profesiId);

        Optional<Apd> existing = repository.findOne(request.getSpecification());
        if (existing.isPresent()) {
            if (existing.get().getIsDeleted()) {
                Apd revived = existing.get();
                revived.setIsDeleted(false);
                return repository.save(revived);
            } else {
                throw new ConflictException("Apd already exists");
            }
        }

        Apd entity = ApdMapper.toEntity(request, profesi);
        return repository.save(entity);
    }

    @Transactional
    public Apd update(Long profesiId, Long id, ApdPostRequest request) {
        Apd existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Apd not found"));

        if (!existing.getProfesi().getId().equals(profesiId)) {
            throw new NotFoundException("Apd not found under given Profesi");
        }
        Profesi profesi = profesiRepository.getReferenceById(profesiId);

        Optional<Apd> duplicate = repository.findOne(request.getSpecification());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new ConflictException("Apd with same nama already exists");
        }

        ApdMapper.updateEntity(existing, request, profesi);
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long profesiId, Long id) {
        Apd existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Apd not found"));
        if (!existing.getProfesi().getId().equals(profesiId)) {
            throw new NotFoundException("Apd not found under given Profesi");
        }
        existing.setIsDeleted(true);
        repository.save(existing);
    }
}
