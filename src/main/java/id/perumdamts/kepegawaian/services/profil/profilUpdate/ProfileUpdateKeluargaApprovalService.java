package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.profil.ProfilKeluarga;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import id.perumdamts.kepegawaian.repositories.profil.jpa.ProfilKeluargaRepository;
import id.perumdamts.kepegawaian.services.revInfo.RevInfoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileUpdateKeluargaApprovalService implements ProfileUpdateApprovalService {
    private static final String UNKNOWN_PROFIL_KELUARGA = "Unknown Profil Keluarga";

    private final RevInfoService service;
    private final ProfilKeluargaRepository repository;

    @Transactional
    @Override
    public void changeHandler(ProfileUpdate profileUpdate, EProfileUpdateApproval approval) {
        log.info("Change Keluarga handler executed");
        if (approval == EProfileUpdateApproval.APPROVED) {
            markAsStable(profileUpdate.getRevId());
            return;
        }

        handleRejectedChange(profileUpdate, profileUpdate.getRevId());
    }

    @Override
    public void revertToPreviousRevision(ProfileUpdate profileUpdate) {
        List<ProfilKeluarga> latestRevision = service.findLatestRevision(ProfilKeluarga.class, Long.valueOf(profileUpdate.getRevId()));
        ProfilKeluarga last = latestRevision.getLast();
        repository.rollbackPrevVersion(
                last.getNik(),
                last.getNama(),
                last.getJenisKelamin().ordinal(),
                last.getAgama().ordinal(),
                last.getHubunganKeluarga().ordinal(),
                last.getTempatLahir(),
                last.getTanggalLahir(),
                last.getTanggungan(),
                last.getPendidikan() != null ? last.getPendidikan().getId() : null,
                last.getStatusPendidikan().ordinal(),
                last.getStatusKawin(),
                last.getNotes(),
                last.getBiodata().getNik(),
                Boolean.FALSE,
                last.getId()
        );
    }

    @Override
    public void handleRejectedChange(ProfileUpdate profileUpdate, String id) {
        Long longId = Long.valueOf(id);
        switch (profileUpdate.getActionType()) {
            case INSERT -> repository.deleteById(longId);
            case UPDATE -> revertToPreviousRevision(profileUpdate);
            case DELETE -> resetEntityState(id);
            default -> throw new IllegalStateException("Unexpected value: " + profileUpdate.getActionType());
        }
    }

    @Override
    public void markAsStable(String id) {
        Long longId = Long.valueOf(id);
        log.info("Marking entity as stable");
        ProfilKeluarga entity = repository.findById(longId)
                .orElseThrow(() -> new IllegalArgumentException(UNKNOWN_PROFIL_KELUARGA));
        entity.setChangedStatus(false);
        repository.save(entity);
    }

    @Override
    public void resetEntityState(String id) {
        Long longId = Long.valueOf(id);
        repository.findById(longId)
                .ifPresent(entity -> {
                    entity.setChangedStatus(false);
                    entity.setIsDeleted(false);
                    repository.save(entity);
                });

    }
}
