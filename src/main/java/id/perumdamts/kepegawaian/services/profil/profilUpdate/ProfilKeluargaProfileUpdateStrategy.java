package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.profil.ProfilKeluarga;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import id.perumdamts.kepegawaian.repositories.profil.jpa.ProfilKeluargaRepository;
import id.perumdamts.kepegawaian.services.revInfo.RevInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfilKeluargaProfileUpdateStrategy implements ProfileUpdateStrategy {
    private final RevInfoService revInfoService;
    private final ProfilKeluargaRepository repository;

    @Override
    public EProfileUpdateTable table() {
        return EProfileUpdateTable.KELUARGA;
    }

    @Override
    public void markAsStable(String id) {
        log.info("Marking Keluarga entity as stable");
        repository.findById(Long.valueOf(id))
                .ifPresent(entity -> {
                    entity.setChangedStatus(false);
                    repository.save(entity);
                });
    }

    @Override
    public void resetEntityState(String id) {
        repository.findAnyById(Long.valueOf(id))
                .ifPresent(entity -> {
                    entity.setChangedStatus(false);
                    entity.setIsDeleted(false);
                    repository.save(entity);
                });
    }

    @Override
    public void handleRejectedInsert(String id) {
        repository.deleteById(Long.valueOf(id));
    }

    @Override
    public void revertToPreviousRevision(ProfileUpdate profileUpdate) {
        List<ProfilKeluarga> latestRevision = revInfoService.findLatestRevision(ProfilKeluarga.class, Long.valueOf(profileUpdate.getRevId()));
        if (latestRevision.isEmpty()) return;
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
}
