package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
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
public class PendidikanProfileUpdateStrategy implements ProfileUpdateStrategy {
    private final RevInfoService revInfoService;
    private final PendidikanRepository repository;
    private final ChangedStatusResolver resolver;

    @Override
    public EProfileUpdateTable table() {
        return EProfileUpdateTable.PENDIDIKAN;
    }

    @Override
    public void markAsStable(String revId) {
        log.info("mark Pendidikan as Stable executed");
        repository.findById(Long.valueOf(revId))
                .ifPresent(pendidikan -> {
                    pendidikan.setChangedStatus(false);
                    // ADR-0035: approve di antrian = disetujui + stamp oleh approver
                    pendidikan.setDisetujui(true);
                    pendidikan.setTanggalDisetujui(LocalDateTime.now());
                    pendidikan.setDisetujuiOleh(resolver.currentUserId());
                    repository.save(pendidikan);
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
    public void handleRejectedInsert(String revId) {
        repository.deleteById(Long.valueOf(revId));
    }

    @Override
    public void revertToPreviousRevision(ProfileUpdate profileUpdate) {
        List<Pendidikan> latestRevision = revInfoService.findLatestRevision(Pendidikan.class, Long.valueOf(profileUpdate.getRevId()));
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
