package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaResponse;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfilUpdateDetail;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.profil.ProfilKeluarga;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import id.perumdamts.kepegawaian.repositories.profil.ProfilKeluargaRepository;
import id.perumdamts.kepegawaian.services.revInfo.RevInfoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileUpdateApprovalServiceImpl implements ProfileUpdateApprovalService {
    private static final String UNKNOWN_PROFIL_KELUARGA = "Unknown Profil Keluarga";

    private final RevInfoService revInfoService;
    private final ProfilKeluargaRepository profilKeluargaRepository;

    @Transactional
    @Override
    public void changeKeluargaHandler(ProfileUpdate profileUpdate, EProfileUpdateApproval approval) {
        log.info("Change Keluarga handler executed");
        if (approval == EProfileUpdateApproval.APPROVED) {
            markAsStable(profileUpdate.getRevId());
            return;
        }

        handleRejectedChange(profileUpdate, profileUpdate.getRevId());
    }

    private void revertToPreviousRevision(ProfileUpdate profileUpdate) {
        ProfilUpdateDetail<ProfilKeluargaResponse> keluargaRevision =
                revInfoService.findKeluargaRevision(profileUpdate);

        ProfilKeluargaResponse previous = keluargaRevision.getPreviousRevision();
        if (previous != null && previous.getId() != null) {
            profilKeluargaRepository.findById(previous.getId())
                    .ifPresent(profilKeluargaRepository::save);
        }
    }

    private void handleRejectedChange(ProfileUpdate profileUpdate, Long id) {
        switch (profileUpdate.getActionType()) {
            case INSERT -> profilKeluargaRepository.deleteById(id);
            case UPDATE -> revertToPreviousRevision(profileUpdate);
            case DELETE -> resetEntityState(id);
            default -> throw new IllegalStateException("Unexpected value: " + profileUpdate.getActionType());
        }
    }

    private void markAsStable(Long id) {
        log.info("Marking entity as stable");
        ProfilKeluarga entity = profilKeluargaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(UNKNOWN_PROFIL_KELUARGA));
        entity.setChangedStatus(false);
        profilKeluargaRepository.save(entity);
    }

    private void resetEntityState(Long id) {
        profilKeluargaRepository.findById(id)
                .ifPresent(entity -> {
                    entity.setChangedStatus(false);
                    entity.setIsDeleted(false);
                    profilKeluargaRepository.save(entity);
                });

    }
}
