package id.perumdamts.kepegawaian.services.profil.pelatihan;

import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanLampiranPostRequest;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanPostRequest;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanPutRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.master.JenisPelatihan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Pelatihan;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.profil.pelatihan.PelatihanMapper;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenisPelatihanRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.BiodataRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.PelatihanRepository;
import id.perumdamts.kepegawaian.services.profil.OwnershipGuard;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilCommandService;
import id.perumdamts.kepegawaian.services.profil.profilUpdate.ProfileUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PelatihanCommandService {
    private static final String UNKNOWN_BIODATA = "Unknown Biodata";
    private static final String UNKNOWN_PELATIHAN = "Unknown Pelatihan";
    private static final String UNKNOWN_JENIS_PELATIHAN = "Unknown Jenis Pelatihan";

    private final PelatihanRepository repository;
    private final BiodataRepository biodataRepository;
    private final JenisPelatihanRepository jenisPelatihanRepository;
    private final LampiranProfilCommandService lampiranProfilCommandService;
    private final ProfileUpdateService profileUpdateService;
    private final OwnershipGuard ownershipGuard;

    @Transactional
    public Long create(PelatihanPostRequest request, boolean requiresApproval) {
        if (requiresApproval) ownershipGuard.assertSelfOwns(request.getBiodataId());
        Biodata biodata = biodataRepository.findById(request.getBiodataId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));
        JenisPelatihan jenisPelatihan = jenisPelatihanRepository
                .findById(request.getJenisPelatihanId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_JENIS_PELATIHAN));

        Pelatihan entity = PelatihanMapper.toEntity(request, biodata, jenisPelatihan);
        entity.setChangedStatus(requiresApproval);

        Pelatihan saved = repository.save(entity);
        handleRevisionUpdate(saved, RevisionMetadata.RevisionType.INSERT);
        return saved.getId();
    }

    @Transactional
    public Long update(Long id, PelatihanPutRequest request, boolean requiresApproval) {
        Pelatihan entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_PELATIHAN));
        if (requiresApproval) ownershipGuard.assertSelfOwns(entity.getBiodata().getNik(), request.getBiodataId());
        Biodata biodata = biodataRepository.findById(request.getBiodataId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));
        JenisPelatihan jenisPelatihan = jenisPelatihanRepository
                .findById(request.getJenisPelatihanId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_JENIS_PELATIHAN));

        Pelatihan updated = PelatihanMapper.updateEntity(entity, request, biodata, jenisPelatihan);
        updated.setChangedStatus(requiresApproval);

        Pelatihan saved = repository.save(updated);
        handleRevisionUpdate(saved, RevisionMetadata.RevisionType.UPDATE);
        return saved.getId();
    }

    @Transactional
    public boolean delete(Long id, boolean requiresApproval) {
        Pelatihan entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_PELATIHAN));
        if (requiresApproval) ownershipGuard.assertSelfOwns(entity.getBiodata().getNik());
        entity.setIsDeleted(true);
        entity.setChangedStatus(requiresApproval);
        repository.save(entity);
        handleRevisionUpdate(entity, RevisionMetadata.RevisionType.DELETE);
        lampiranProfilCommandService.deleteByRefId(EJenisLampiranProfil.PROFIL_PELATIHAN, id);
        return true;
    }



    @Transactional
    public Long addLampiran(PelatihanLampiranPostRequest request, boolean requiresApproval) {
        if (!repository.existsById(request.getRefId()))
            throw new NotFoundException(UNKNOWN_PELATIHAN);
        lampiranProfilCommandService.addLampiran(request, requiresApproval);
        return request.getRefId();
    }

    @Transactional
    public boolean deleteLampiran(Long id, boolean requiresApproval) {
        lampiranProfilCommandService.deleteById(id, requiresApproval);
        return true;
    }

    private void handleRevisionUpdate(Pelatihan saved, RevisionMetadata.RevisionType type) {
        if (Boolean.FALSE.equals(saved.getChangedStatus())) return;
        profileUpdateService.create(String.valueOf(saved.getId()), type, EProfileUpdateTable.PELATIHAN);
    }
}