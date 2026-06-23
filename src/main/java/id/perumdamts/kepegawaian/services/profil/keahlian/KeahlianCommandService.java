package id.perumdamts.kepegawaian.services.profil.keahlian;

import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianLampiranPostRequest;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianPostRequest;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianPutRequest;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.master.JenisKeahlian;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Keahlian;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenisKeahlianRepository;
import id.perumdamts.kepegawaian.repositories.profil.BiodataRepository;
import id.perumdamts.kepegawaian.repositories.profil.KeahlianRepository;
import id.perumdamts.kepegawaian.services.profil.ChangedStatusResolver;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilService;
import id.perumdamts.kepegawaian.services.profil.profilUpdate.ProfileUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeahlianCommandService {
    private static final String UNKNOWN_BIODATA = "Unknown Biodata";
    private static final String UNKNOWN_KEAHLIAN = "Unknown Keahlian";
    private static final String UNKNOWN_JENIS_KEAHLIAN = "Unknown Jenis Keahlian";

    private final KeahlianRepository repository;
    private final BiodataRepository biodataRepository;
    private final JenisKeahlianRepository jenisKeahlianRepository;
    private final LampiranProfilService lampiranProfilService;
    private final ProfileUpdateService profileUpdateService;
    private final ChangedStatusResolver resolver;

    @Transactional
    public Long create(KeahlianPostRequest request) {
        Biodata biodata = biodataRepository.findById(request.getBiodataId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));
        JenisKeahlian jenisKeahlian = jenisKeahlianRepository.findById(request.getKeahlianId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_JENIS_KEAHLIAN));
        Keahlian keahlian = KeahlianPostRequest.toEntity(request, biodata, jenisKeahlian);
        keahlian.setChangedStatus(resolver.requiresApproval());
        Keahlian save = repository.save(keahlian);
        handleRevisionUpdate(save, RevisionMetadata.RevisionType.INSERT);
        return save.getId();
    }

    @Transactional
    public Long update(Long id, KeahlianPutRequest request) {
        Keahlian keahlian = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_KEAHLIAN));
        Biodata biodata = biodataRepository.findById(request.getBiodataId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));
        JenisKeahlian jenisKeahlian = jenisKeahlianRepository.findById(request.getKeahlianId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_JENIS_KEAHLIAN));
        Keahlian entity = KeahlianPutRequest.toEntity(request, keahlian, biodata, jenisKeahlian);
        entity.setChangedStatus(resolver.requiresApproval());
        Keahlian save = repository.save(entity);
        handleRevisionUpdate(save, RevisionMetadata.RevisionType.UPDATE);
        return save.getId();
    }

    @Transactional
    public void delete(Long id) {
        Keahlian entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_KEAHLIAN));
        entity.setIsDeleted(true);
        entity.setChangedStatus(resolver.requiresApproval());
        repository.save(entity);
        handleRevisionUpdate(entity, RevisionMetadata.RevisionType.DELETE);
        lampiranProfilService.deleteByRefId(EJenisLampiranProfil.PROFIL_KEAHLIAN, id);
    }

    // Lampiran delegates
    public List<LampiranProfilResponse> getLampiran(Long id) {
        return lampiranProfilService.getLampiran(EJenisLampiranProfil.PROFIL_KEAHLIAN, id);
    }

    public LampiranProfilResponse getLampiranById(Long id) {
        return lampiranProfilService.getLampiranById(id);
    }

    public ResponseEntity<?> getFileLampiranById(Long id) {
        return lampiranProfilService.getFileLampiranById(EJenisLampiranProfil.PROFIL_KEAHLIAN, id);
    }

    @Transactional
    public Long addLampiran(KeahlianLampiranPostRequest request) {
        boolean exists = repository.existsById(request.getRefId());
        if (!exists)
            throw new NotFoundException(UNKNOWN_KEAHLIAN);
        lampiranProfilService.addLampiran(request);
        return request.getRefId();
    }

    @Transactional
    public void deleteLampiran(Long id) {
        lampiranProfilService.deleteById(id);
    }

    private void handleRevisionUpdate(Keahlian save, RevisionMetadata.RevisionType type) {
        if (Boolean.FALSE.equals(save.getChangedStatus())) return;
        profileUpdateService.create(save.getId(), type, EProfileUpdateTable.KEAHLIAN);
    }
}