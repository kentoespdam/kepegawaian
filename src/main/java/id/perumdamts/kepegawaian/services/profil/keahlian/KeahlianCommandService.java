package id.perumdamts.kepegawaian.services.profil.keahlian;

import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianLampiranPostRequest;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianPostRequest;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianPutRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.master.JenisKeahlian;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Keahlian;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.profil.keahlian.KeahlianMapper;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenisKeahlianRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.BiodataRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.KeahlianRepository;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilCommandService;
import id.perumdamts.kepegawaian.services.profil.profilUpdate.ProfileUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KeahlianCommandService {
    private static final String UNKNOWN_BIODATA = "Unknown Biodata";
    private static final String UNKNOWN_KEAHLIAN = "Unknown Keahlian";
    private static final String UNKNOWN_JENIS_KEAHLIAN = "Unknown Jenis Keahlian";

    private final KeahlianRepository repository;
    private final BiodataRepository biodataRepository;
    private final JenisKeahlianRepository jenisKeahlianRepository;
    private final LampiranProfilCommandService lampiranProfilCommandService;
    private final ProfileUpdateService profileUpdateService;

    @Transactional
    public Long create(KeahlianPostRequest request, boolean requiresApproval) {
        Biodata biodata = biodataRepository.findById(request.getBiodataId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));
        JenisKeahlian jenisKeahlian = jenisKeahlianRepository.findById(request.getKeahlianId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_JENIS_KEAHLIAN));
        Keahlian keahlian = KeahlianMapper.toEntity(request, biodata, jenisKeahlian);
        keahlian.setChangedStatus(requiresApproval);
        Keahlian save = repository.save(keahlian);
        handleRevisionUpdate(save, RevisionMetadata.RevisionType.INSERT);
        return save.getId();
    }

    @Transactional
    public Long update(Long id, KeahlianPutRequest request, boolean requiresApproval) {
        Keahlian keahlian = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_KEAHLIAN));
        Biodata biodata = biodataRepository.findById(request.getBiodataId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));
        JenisKeahlian jenisKeahlian = jenisKeahlianRepository.findById(request.getKeahlianId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_JENIS_KEAHLIAN));
        Keahlian entity = KeahlianMapper.updateEntity(keahlian, request, biodata, jenisKeahlian);
        entity.setChangedStatus(requiresApproval);
        Keahlian save = repository.save(entity);
        handleRevisionUpdate(save, RevisionMetadata.RevisionType.UPDATE);
        return save.getId();
    }

    @Transactional
    public boolean delete(Long id, boolean requiresApproval) {
        Keahlian entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_KEAHLIAN));
        entity.setIsDeleted(true);
        entity.setChangedStatus(requiresApproval);
        repository.save(entity);
        handleRevisionUpdate(entity, RevisionMetadata.RevisionType.DELETE);
        lampiranProfilCommandService.deleteByRefId(EJenisLampiranProfil.PROFIL_KEAHLIAN, id);
        return true;
    }



    @Transactional
    public Long addLampiran(KeahlianLampiranPostRequest request, boolean requiresApproval) {
        boolean exists = repository.existsById(request.getRefId());
        if (!exists)
            throw new NotFoundException(UNKNOWN_KEAHLIAN);
        lampiranProfilCommandService.addLampiran(request, requiresApproval);
        return request.getRefId();
    }

    @Transactional
    public boolean deleteLampiran(Long id, boolean requiresApproval) {
        lampiranProfilCommandService.deleteById(id, requiresApproval);
        return true;
    }

    private void handleRevisionUpdate(Keahlian save, RevisionMetadata.RevisionType type) {
        if (Boolean.FALSE.equals(save.getChangedStatus())) return;
        profileUpdateService.create(String.valueOf(save.getId()), type, EProfileUpdateTable.KEAHLIAN);
    }
}