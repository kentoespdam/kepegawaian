package id.perumdamts.kepegawaian.services.master.organisasi;

import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiPostRequest;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.OrganisasiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganisasiCommandService {
    private final OrganisasiRepository repository;

    @Transactional
    public Organisasi create(OrganisasiPostRequest request) {
        Organisasi parent = findParent(request.getParentId());
        Optional<Organisasi> existing = repository.findOne(request.getSpecification());
        if (existing.isPresent()) {
            if (existing.get().getIsDeleted()) {
                Organisasi revived = existing.get();
                revived.setIsDeleted(false);
                OrganisasiMapper.updateEntity(revived, request, parent);
                return repository.save(revived);
            }
            throw new ConflictException("Organisasi already exists");
        }
        Organisasi entity = OrganisasiMapper.toEntity(request, parent);
        return repository.save(entity);
    }

    @Transactional
    public Organisasi update(Long id, OrganisasiPostRequest request) {
        Organisasi existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Organisasi not found"));
        Organisasi parent = findParent(request.getParentId());
        Optional<Organisasi> duplicate = repository.findOne(request.getSpecification());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new ConflictException("Organisasi already exists");
        }
        OrganisasiMapper.updateEntity(existing, request, parent);
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Organisasi existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Organisasi not found"));
        existing.setIsDeleted(true);
        repository.save(existing);
    }

    private Organisasi findParent(Long parentId) {
        if (parentId == null) return null;
        return repository.findById(parentId)
                .orElseThrow(() -> new NotFoundException("Parent organisasi not found"));
    }
}
