package id.perumdamts.kepegawaian.services.profil.pendidikan;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilResponse;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanLampiranPostRequest;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanPostRequest;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanPutRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenjangPendidikanRepository;
import id.perumdamts.kepegawaian.repositories.profil.BiodataRepository;
import id.perumdamts.kepegawaian.repositories.profil.PendidikanRepository;
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
public class PendidikanCommandService {
    private static final String UNKNOWN_BIODATA = "Unknown Biodata";
    private static final String UNKNOWN_PENDIDIKAN = "Unknown Pendidikan";
    private static final String UNKNOWN_JENJANG = "Unknown Jenjang Pendidikan";

    private final PendidikanRepository repository;
    private final BiodataRepository biodataRepository;
    private final JenjangPendidikanRepository jenjangPendidikanRepository;
    private final LampiranProfilService lampiranProfilService;
    private final ProfileUpdateService profileUpdateService;
    private final ChangedStatusResolver resolver;

    @Transactional
    public Long create(PendidikanPostRequest request) {
        Biodata biodata = biodataRepository.findById(request.getBiodataId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));
        JenjangPendidikan jenjangPendidikan = jenjangPendidikanRepository
                .findById(request.getJenjangPendidikanId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_JENJANG));

        Pendidikan pendidikan = PendidikanPostRequest.from(request, biodata, jenjangPendidikan);
        pendidikan.setChangedStatus(resolver.requiresApproval());

        Pendidikan save = repository.save(pendidikan);
        handleUpdateIsLatest(request.getIsLatest(), save.getId(), biodata, jenjangPendidikan);
        handleRevisionUpdate(save, RevisionMetadata.RevisionType.INSERT);
        return save.getId();
    }

    @Transactional
    public Long update(Long id, PendidikanPutRequest request) {
        Pendidikan pendidikan = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_PENDIDIKAN));
        Biodata biodata = biodataRepository.findById(request.getBiodataId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));
        JenjangPendidikan jenjangPendidikan = jenjangPendidikanRepository
                .findById(request.getJenjangPendidikanId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_JENJANG));

        Pendidikan entity = PendidikanPutRequest.from(request, pendidikan, biodata, jenjangPendidikan);
        entity.setChangedStatus(resolver.requiresApproval());

        Pendidikan save = repository.save(entity);
        handleUpdateIsLatest(request.getIsLatest(), save.getId(), biodata, jenjangPendidikan);
        handleRevisionUpdate(save, RevisionMetadata.RevisionType.UPDATE);
        return save.getId();
    }

    @Transactional
    public void delete(Long id) {
        Pendidikan entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_PENDIDIKAN));
        entity.setIsDeleted(true);
        entity.setChangedStatus(resolver.requiresApproval());
        repository.save(entity);

        handleRevisionUpdate(entity, RevisionMetadata.RevisionType.DELETE);
        lampiranProfilService.deleteByRefId(EJenisLampiranProfil.PROFIL_PENDIDIKAN, id);
    }

    // Lampiran delegates

    public List<LampiranProfilResponse> getLampiran(Long id) {
        return lampiranProfilService.getLampiran(EJenisLampiranProfil.PROFIL_PENDIDIKAN, id);
    }

    public LampiranProfilResponse getLampiranById(Long id) {
        return lampiranProfilService.getLampiranById(id);
    }

    public ResponseEntity<?> getFileLampiranById(Long id) {
        return lampiranProfilService.getFileLampiranById(EJenisLampiranProfil.PROFIL_PENDIDIKAN, id);
    }

    @Transactional
    public Long addLampiran(PendidikanLampiranPostRequest request) {
        boolean exists = repository.existsById(request.getRefId());
        if (!exists)
            throw new NotFoundException(UNKNOWN_PENDIDIKAN);
        lampiranProfilService.addLampiran(request);
        return request.getRefId();
    }

    @Transactional
    public void deleteLampiran(Long id) {
        lampiranProfilService.deleteById(id);
    }

    // Private helpers

    private void handleUpdateIsLatest(Boolean isLatest, Long id, Biodata biodata, JenjangPendidikan jenjangPendidikan) {
        if (Boolean.FALSE.equals(isLatest)) return;
        repository.updateIsLatest(id, biodata.getNik());
        biodata.setPendidikanTerakhir(jenjangPendidikan);
        biodataRepository.save(biodata);
    }

    private void handleRevisionUpdate(Pendidikan save, RevisionMetadata.RevisionType type) {
        if (Boolean.FALSE.equals(save.getChangedStatus())) return;
        profileUpdateService.create(save.getId(), type, EProfileUpdateTable.PENDIDIKAN);
    }
}
