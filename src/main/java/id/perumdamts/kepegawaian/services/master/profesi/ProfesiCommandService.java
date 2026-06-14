package id.perumdamts.kepegawaian.services.master.profesi;

import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiPostRequest;
import id.perumdamts.kepegawaian.entities.master.Grade;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jpa.GradeRepository;
import id.perumdamts.kepegawaian.repositories.master.JabatanRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.OrganisasiRepository;
import id.perumdamts.kepegawaian.repositories.master.ProfesiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfesiCommandService {
    private final ProfesiRepository repository;
    private final OrganisasiRepository organisasiRepository;
    private final JabatanRepository jabatanRepository;
    private final GradeRepository gradeRepository;

    @Transactional
    public Profesi create(ProfesiPostRequest request) {
        Jabatan jabatan = findJabatan(request.getJabatanId());

        // Revive-on-create: peek across archived rows (bypasses @SQLRestriction).
        Optional<Profesi> archived = repository
                .findFirstByNamaAndJabatan_IdAndGrade_IdAndIsDeletedTrue(
                        request.getNama(), request.getJabatanId(), request.getGradeId());
        if (archived.isPresent()) {
            Profesi revived = archived.get();
            revived.setIsDeleted(false);
            applyFields(revived, request, jabatan);
            return repository.save(revived);
        }

        // Live collision.
        Optional<Profesi> live = repository
                .findFirstByNamaAndJabatan_IdAndGrade_IdAndIsDeletedFalse(
                        request.getNama(), request.getJabatanId(), request.getGradeId());
        if (live.isPresent()) {
            throw new ConflictException("Profesi already exists");
        }

        Profesi entity = ProfesiMapper.toEntity(request,
                findOrganisasi(request.getOrganisasiId()),
                jabatan,
                findGrade(request.getGradeId()));
        return repository.save(entity);
    }

    @Transactional
    public Profesi update(Long id, ProfesiPostRequest request) {
        Profesi existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Profesi not found"));

        // Live collision (different id).
        Optional<Profesi> liveDup = repository
                .findFirstByNamaAndJabatan_IdAndGrade_IdAndIsDeletedFalse(
                        request.getNama(), request.getJabatanId(), request.getGradeId());
        if (liveDup.isPresent() && !liveDup.get().getId().equals(id)) {
            throw new ConflictException("Profesi with same combination already exists");
        }

        // Archived collision (CONTEXT.md: edit must not revive; revive is create-only).
        Optional<Profesi> archivedDup = repository
                .findFirstByNamaAndJabatan_IdAndGrade_IdAndIsDeletedTrue(
                        request.getNama(), request.getJabatanId(), request.getGradeId());
        if (archivedDup.isPresent() && !archivedDup.get().getId().equals(id)) {
            throw new ConflictException(
                    "Profesi with same combination is archived; create a new one to revive");
        }

        Jabatan jabatan = findJabatan(request.getJabatanId());
        applyFields(existing, request, jabatan);
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Profesi existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Profesi not found"));
        existing.setIsDeleted(true);
        repository.save(existing);
    }

    private void applyFields(Profesi entity, ProfesiPostRequest request, Jabatan jabatan) {
        entity.setOrganisasi(findOrganisasi(request.getOrganisasiId()));
        entity.setJabatan(jabatan);
        entity.setLevel(jabatan.getLevel());
        entity.setGrade(findGrade(request.getGradeId()));
        entity.setNama(request.getNama());
        entity.setDetail(request.getDetail());
        entity.setResiko(request.getResiko());
    }

    private Jabatan findJabatan(Long id) {
        if (id == null) throw new NotFoundException("Jabatan not found");
        return jabatanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Jabatan not found"));
    }

    private Organisasi findOrganisasi(Long id) {
        if (id == null) throw new NotFoundException("Organisasi not found");
        return organisasiRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Organisasi not found"));
    }

    private Grade findGrade(Long id) {
        if (id == null) throw new NotFoundException("Grade not found");
        return gradeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Grade not found"));
    }
}
