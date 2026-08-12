package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import id.perumdamts.kepegawaian.repositories.profil.jpa.PendidikanRepository;
import id.perumdamts.kepegawaian.services.profil.ChangedStatusResolver;
import id.perumdamts.kepegawaian.services.revInfo.RevInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileUpdatePendidikanApprovalService implements ProfileUpdateApprovalService {
    private static final String UNKNOWN_PROFIL_PENDIDIKAN = "Unknown Profil Pendidikan";
    private final RevInfoService service;
    private final PendidikanRepository repository;
    private final ChangedStatusResolver resolver;

    @Override
    public void changeHandler(ProfileUpdate profileUpdate, EProfileUpdateApproval approval) {
        if (approval == EProfileUpdateApproval.APPROVED) {
            markAsStable(profileUpdate.getRevId());
            return;
        }
        handleRejectedChange(profileUpdate, profileUpdate.getRevId());
    }

    @Override
    public void markAsStable(String revId) {
        log.info("mark as Stable executed");
        try {
            Long id = Long.valueOf(revId);
            repository.findById(id)
                    .ifPresent(pendidikan -> {
                        pendidikan.setChangedStatus(false);
                        // ADR-0035: approve di antrian = disetujui + stamp oleh approver
                        pendidikan.setDisetujui(true);
                        pendidikan.setTanggalDisetujui(LocalDateTime.now());
                        pendidikan.setDisetujuiOleh(resolver.currentUserId());
                        repository.save(pendidikan);
                    });
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(UNKNOWN_PROFIL_PENDIDIKAN);
        }
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

    @Override
    public void handleRejectedChange(ProfileUpdate profileUpdate, String revId) {
        Long longId = Long.valueOf(revId);
        switch (profileUpdate.getActionType()) {
            case INSERT -> repository.deleteById(longId);
            case UPDATE -> revertToPreviousRevision(profileUpdate);
            case DELETE -> resetEntityState(revId);
            default -> throw new IllegalStateException("Unexpected value: " + profileUpdate.getActionType());
        }
    }

    @Override
    public void revertToPreviousRevision(ProfileUpdate profileUpdate) {
        List<Pendidikan> latestRevision = service.findLatestRevision(Pendidikan.class, Long.valueOf(profileUpdate.getRevId()));
        Pendidikan last = latestRevision.getLast();
        repository.rollbackPrevVersion(
                last.getBiodata().getNik(),
                last.getJenjangPendidikan().getId(),
                last.getGelarDepan(),
                last.getGelarBelakang(),
                last.getJurusan(),
                last.getInstitusi(),
                last.getKota(),
                last.getTahunMasuk(),
                last.getIsLulus(),
                last.getTahunLulus(),
                last.getGpa(),
                last.getIsLatest(),
                false,
                last.getDisetujui(),
                last.getTanggalPengajuan(),
                last.getTanggalDisetujui(),
                last.getDisetujuiOleh(),
                last.getId()
        );
    }
}
