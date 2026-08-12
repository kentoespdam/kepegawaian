package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.profil.Keahlian;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenisKeahlianRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.BiodataRepository;
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
    private final BiodataRepository biodataRepository;
    private final JenisKeahlianRepository jenisKeahlianRepository;

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
        // Salin hanya id relasi dari entity audit (session Envers), re-attach via
        // getReferenceById di session saat ini — hindari proxy lintas session (bd kepegawaian-yu5j).
        entity.setBiodata(last.getBiodata() != null
                ? biodataRepository.getReferenceById(last.getBiodata().getNik())
                : null);
        entity.setJenisKeahlian(last.getJenisKeahlian() != null
                ? jenisKeahlianRepository.getReferenceById(last.getJenisKeahlian().getId())
                : null);
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
