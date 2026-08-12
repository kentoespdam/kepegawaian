package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.profil.Keahlian;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import id.perumdamts.kepegawaian.repositories.profil.jpa.KeahlianRepository;
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
public class KeahlianProfileUpdateStrategy implements ProfileUpdateStrategy {
    private final RevInfoService revInfoService;
    private final KeahlianRepository repository;
    private final ChangedStatusResolver resolver;

    @Override
    public EProfileUpdateTable table() {
        return EProfileUpdateTable.KEAHLIAN;
    }

    @Override
    public void markAsStable(String revId) {
        log.info("mark Keahlian as Stable executed");
        repository.findById(Long.valueOf(revId))
                .ifPresent(keahlian -> {
                    keahlian.setChangedStatus(false);
                    keahlian.setDisetujui(true);
                    keahlian.setTanggalDisetujui(LocalDateTime.now());
                    keahlian.setDisetujuiOleh(resolver.currentUserId());
                    repository.save(keahlian);
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
        List<Keahlian> latestRevision = revInfoService.findLatestRevision(Keahlian.class, Long.valueOf(profileUpdate.getRevId()));
        if (latestRevision.isEmpty()) return;
        Keahlian last = latestRevision.getLast();
        Keahlian entity = repository.findById(Long.valueOf(profileUpdate.getRevId()))
                .orElseThrow(() -> new IllegalArgumentException("Unknown Keahlian"));
        entity.setBiodata(last.getBiodata());
        entity.setJenisKeahlian(last.getJenisKeahlian());
        entity.setKualifikasi(last.getKualifikasi());
        entity.setSertifikasi(last.getSertifikasi());
        entity.setInstitusi(last.getInstitusi());
        entity.setTahun(last.getTahun());
        entity.setMasaBerlaku(last.getMasaBerlaku());
        entity.setDisetujui(last.getDisetujui());
        entity.setTanggalPengajuan(last.getTanggalPengajuan());
        entity.setTanggalDisetujui(last.getTanggalDisetujui());
        entity.setDisetujuiOleh(last.getDisetujuiOleh());
        entity.setChangedStatus(false);
        repository.save(entity);
    }
}
