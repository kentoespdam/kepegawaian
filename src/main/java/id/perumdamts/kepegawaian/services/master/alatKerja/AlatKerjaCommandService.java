package id.perumdamts.kepegawaian.services.master.alatKerja;

import id.perumdamts.kepegawaian.dto.master.alatKerja.AlatKerjaPostRequest;
import id.perumdamts.kepegawaian.entities.master.AlatKerja;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.master.alatKerja.AlatKerjaMapper;
import id.perumdamts.kepegawaian.repositories.master.jpa.AlatKerjaRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.ProfesiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlatKerjaCommandService {
    private final AlatKerjaRepository repository;
    private final ProfesiRepository profesiRepository;

    @Transactional
    public AlatKerja create(AlatKerjaPostRequest request) {
        Long profesiId = request.getProfesiId();
        if (!profesiRepository.existsById(profesiId)) {
            throw new NotFoundException("Profesi not found");
        }
        Profesi profesi = profesiRepository.getReferenceById(profesiId);

        Optional<AlatKerja> existing = repository.findOne(request.getSpecification());
        if (existing.isPresent()) {
            if (existing.get().getIsDeleted()) {
                AlatKerja revived = existing.get();
                revived.setIsDeleted(false);
                return repository.save(revived);
            } else {
                throw new ConflictException("AlatKerja already exists");
            }
        }

        AlatKerja entity = AlatKerjaMapper.toEntity(request, profesi);
        return repository.save(entity);
    }

    @Transactional
    public AlatKerja update(Long id, AlatKerjaPostRequest request) {
        Long profesiId = request.getProfesiId();
        AlatKerja existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("AlatKerja not found"));

        if (!existing.getProfesi().getId().equals(profesiId)) {
            throw new NotFoundException("AlatKerja not found under given Profesi");
        }
        Profesi profesi = profesiRepository.getReferenceById(profesiId);

        Optional<AlatKerja> duplicate = repository.findOne(request.getSpecification());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new ConflictException("AlatKerja with same nama already exists");
        }

        AlatKerjaMapper.updateEntity(existing, request, profesi);
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        AlatKerja existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("AlatKerja not found"));
        existing.setIsDeleted(true);
        repository.save(existing);
    }
}
