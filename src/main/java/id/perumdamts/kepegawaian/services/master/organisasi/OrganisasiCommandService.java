package id.perumdamts.kepegawaian.services.master.organisasi;

import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiPostRequest;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.master.organisasi.OrganisasiMapper;
import id.perumdamts.kepegawaian.repositories.master.jpa.OrganisasiRepository;
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
        // Native carcass-finder — JpaSpecificationExecutor.findOne() respects
        // @SQLRestriction and would hide soft-deleted rows, so the revive branch
        // below would be dead code (see kepegawaian-33s, ADR-0005).
        Optional<Organisasi> existing = repository.findAnyByUniqueKey(request.getNama(), request.getParentId());
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

        // Live collision (different id).
        Optional<Organisasi> duplicate = repository.findOne(request.uniquenessSpecification());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new ConflictException("Organisasi already exists");
        }

        // Archived collision (CONTEXT.md: edit must not revive; revive is create-only).
        Optional<Organisasi> archived = repository.findAnyByUniqueKey(request.getNama(), request.getParentId());
        if (archived.isPresent() && archived.get().getIsDeleted() && !archived.get().getId().equals(id)) {
            throw new ConflictException(
                    "Organisasi with same combination is archived; create a new one to revive");
        }

        OrganisasiMapper.updateEntity(existing, request, parent);
        return repository.save(existing);
    }

    @Transactional
    public boolean delete(Long id) {
        Organisasi existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Organisasi not found"));
        if (repository.existsByParentIdAndIsDeletedFalse(id)) {
            throw new ConflictException("Organisasi masih memiliki sub-organisasi");
        }
        existing.setIsDeleted(true);
        repository.save(existing);
        return true;
    }

    private Organisasi findParent(Long parentId) {
        if (parentId == null) return null;
        return repository.findById(parentId)
                .orElseThrow(() -> new NotFoundException("Parent organisasi not found"));
    }
}
