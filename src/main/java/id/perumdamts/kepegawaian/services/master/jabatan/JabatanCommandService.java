package id.perumdamts.kepegawaian.services.master.jabatan;

import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanPostRequest;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.JabatanRepository;
import id.perumdamts.kepegawaian.repositories.master.LevelRepository;
import id.perumdamts.kepegawaian.repositories.master.OrganisasiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JabatanCommandService {
    private final JabatanRepository repository;
    private final OrganisasiRepository organisasiRepository;
    private final LevelRepository levelRepository;

    @Transactional
    public Jabatan create(JabatanPostRequest request) {
        Jabatan parent = findParent(request.getParentId());
        Organisasi organisasi = findOrganisasi(request.getOrganisasiId());
        Level level = findLevel(request.getLevelId());
        Optional<Jabatan> existing = repository.findOne(request.getSpecification());
        if (existing.isPresent()) {
            if (existing.get().getIsDeleted()) {
                Jabatan revived = existing.get();
                revived.setIsDeleted(false);
                JabatanMapper.updateEntity(revived, request, parent, organisasi, level);
                return repository.save(revived);
            }
            throw new ConflictException("Jabatan already exists");
        }
        Jabatan entity = JabatanMapper.toEntity(request, parent, organisasi, level);
        return repository.save(entity);
    }

    @Transactional
    public Jabatan update(Long id, JabatanPostRequest request) {
        Jabatan existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Jabatan not found"));
        Jabatan parent = findParent(request.getParentId());
        Organisasi organisasi = findOrganisasi(request.getOrganisasiId());
        Level level = findLevel(request.getLevelId());
        Optional<Jabatan> duplicate = repository.findOne(request.getSpecification());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new ConflictException("Jabatan already exists");
        }
        JabatanMapper.updateEntity(existing, request, parent, organisasi, level);
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Jabatan existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Jabatan not found"));
        existing.setIsDeleted(true);
        repository.save(existing);
    }

    private Jabatan findParent(Long parentId) {
        if (parentId == null) return null;
        return repository.findById(parentId)
                .orElseThrow(() -> new NotFoundException("Parent jabatan not found"));
    }

    private Organisasi findOrganisasi(Long id) {
        if (id == null) return null;
        return organisasiRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Organisasi not found"));
    }

    private Level findLevel(Long id) {
        if (id == null) return null;
        return levelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Level not found"));
    }
}
