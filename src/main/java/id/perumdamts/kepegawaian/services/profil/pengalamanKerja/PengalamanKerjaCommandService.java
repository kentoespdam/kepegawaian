package id.perumdamts.kepegawaian.services.profil.pengalamanKerja;

import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaPostRequest;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaPutRequest;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanLampiranPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.PengalamanKerja;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.profil.pengalamanKerja.PengalamanKerjaMapper;
import id.perumdamts.kepegawaian.repositories.profil.jpa.BiodataRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.PengalamanKerjaRepository;
import id.perumdamts.kepegawaian.services.profil.OwnershipGuard;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilCommandService;
import id.perumdamts.kepegawaian.services.profil.profilUpdate.ProfileUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PengalamanKerjaCommandService {
    private static final String UNKNOWN_BIODATA = "Unknown Biodata";
    private static final String UNKNOWN_PENGALAMAN_KERJA = "Unknown Pengalaman Kerja";

    private final PengalamanKerjaRepository repository;
    private final BiodataRepository biodataRepository;
    private final LampiranProfilCommandService lampiranProfilCommandService;
    private final ProfileUpdateService profileUpdateService;
    private final OwnershipGuard ownershipGuard;

    @Transactional
    public Long create(PengalamanKerjaPostRequest request, boolean requiresApproval) {
        if (requiresApproval) ownershipGuard.assertSelfOwns(request.getBiodataId());
        Biodata biodata = biodataRepository.findById(request.getBiodataId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));
        PengalamanKerja entity = PengalamanKerjaMapper.toEntity(request, biodata);
        entity.setChangedStatus(requiresApproval);
        PengalamanKerja save = repository.save(entity);
        handleRevisionUpdate(save, RevisionMetadata.RevisionType.INSERT);
        return save.getId();
    }

    @Transactional
    public Long update(Long id, PengalamanKerjaPutRequest request, boolean requiresApproval) {
        PengalamanKerja entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_PENGALAMAN_KERJA));
        if (requiresApproval) ownershipGuard.assertSelfOwns(entity.getBiodata().getNik(), request.getBiodataId());
        Biodata biodata = biodataRepository.findById(request.getBiodataId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));
        PengalamanKerja updated = PengalamanKerjaMapper.updateEntity(entity, request, biodata);
        updated.setChangedStatus(requiresApproval);
        PengalamanKerja save = repository.save(updated);
        handleRevisionUpdate(save, RevisionMetadata.RevisionType.UPDATE);
        return save.getId();
    }

    @Transactional
    public boolean delete(Long id, boolean requiresApproval) {
        PengalamanKerja entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_PENGALAMAN_KERJA));
        if (requiresApproval) ownershipGuard.assertSelfOwns(entity.getBiodata().getNik());
        entity.setIsDeleted(true);
        entity.setChangedStatus(requiresApproval);
        repository.save(entity);
        handleRevisionUpdate(entity, RevisionMetadata.RevisionType.DELETE);
        lampiranProfilCommandService.deleteByRefId(EJenisLampiranProfil.PROFIL_PENGALAMAN_KERJA, id);
        return true;
    }



    @Transactional
    public Long addLampiran(PengalamanLampiranPostRequest request, boolean requiresApproval) {
        if (!repository.existsById(request.getRefId())) {
            throw new NotFoundException(UNKNOWN_PENGALAMAN_KERJA);
        }
        lampiranProfilCommandService.addLampiran(request, requiresApproval);
        return request.getRefId();
    }

    public boolean deleteLampiran(Long id, boolean requiresApproval) {
        lampiranProfilCommandService.deleteById(id, requiresApproval);
        return true;
    }

    private void handleRevisionUpdate(PengalamanKerja save, RevisionMetadata.RevisionType type) {
        if (Boolean.FALSE.equals(save.getChangedStatus())) return;
        profileUpdateService.create(String.valueOf(save.getId()), type, EProfileUpdateTable.PENGALAMAN_KERJA);
    }
}
