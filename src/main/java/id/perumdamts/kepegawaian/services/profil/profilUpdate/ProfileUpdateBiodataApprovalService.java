package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import id.perumdamts.kepegawaian.repositories.profil.jpa.BiodataRepository;
import id.perumdamts.kepegawaian.services.revInfo.RevInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileUpdateBiodataApprovalService implements ProfileUpdateApprovalService {
    private static final String UNKNOWN_BIODATA = "Unknown Biodata";

    private final RevInfoService service;
    private final BiodataRepository repository;

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
        log.info("mark Biodata as Stable executed");
        repository.findById(revId)
                .ifPresent(biodata -> {
                    biodata.setChangedStatus(false);
                    repository.save(biodata);
                });
    }

    @Override
    public void resetEntityState(String id) {
        repository.findById(id)
                .ifPresent(entity -> {
                    entity.setChangedStatus(false);
                    entity.setIsDeleted(false);
                    repository.save(entity);
                });
    }

    @Override
    public void handleRejectedChange(ProfileUpdate profileUpdate, String revId) {
        switch (profileUpdate.getActionType()) {
            case INSERT -> repository.deleteById(revId);
            case UPDATE -> revertToPreviousRevision(profileUpdate);
            case DELETE -> resetEntityState(revId);
            default -> throw new IllegalStateException("Unexpected value: " + profileUpdate.getActionType());
        }
    }

    @Override
    public void revertToPreviousRevision(ProfileUpdate profileUpdate) {
        String revId = profileUpdate.getRevId();
        List<Biodata> latestRevision = service.findLatestRevision(Biodata.class, revId);
        if (latestRevision.isEmpty()) return;
        Biodata last = latestRevision.getLast();
        // Rollback biodata fields from Envers revision
        Biodata entity = repository.findById(revId)
                .orElseThrow(() -> new IllegalArgumentException(UNKNOWN_BIODATA));
        entity.setNama(last.getNama());
        entity.setJenisKelamin(last.getJenisKelamin());
        entity.setTempatLahir(last.getTempatLahir());
        entity.setTanggalLahir(last.getTanggalLahir());
        entity.setAgama(last.getAgama());
        entity.setStatusKawin(last.getStatusKawin());
        entity.setAlamat(last.getAlamat());
        entity.setTelp(last.getTelp());
        entity.setIbuKandung(last.getIbuKandung());
        entity.setPendidikanTerakhir(last.getPendidikanTerakhir());
        entity.setChangedStatus(false);
        repository.save(entity);
    }
}
