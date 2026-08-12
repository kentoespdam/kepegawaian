package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenisKitasRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.BiodataRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.KartuIdentitasRepository;
import id.perumdamts.kepegawaian.services.revInfo.RevInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KartuIdentitasProfileUpdateStrategy implements ProfileUpdateStrategy {
    private final RevInfoService revInfoService;
    private final KartuIdentitasRepository repository;
    private final BiodataRepository biodataRepository;
    private final JenisKitasRepository jenisKitasRepository;

    @Override
    public EProfileUpdateTable table() {
        return EProfileUpdateTable.KARTU_IDENTITAS;
    }

    @Override
    public void markAsStable(String revId) {
        log.info("mark KartuIdentitas as Stable executed");
        repository.findById(Long.valueOf(revId))
                .ifPresent(kartuIdentitas -> {
                    kartuIdentitas.setChangedStatus(false);
                    repository.save(kartuIdentitas);
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
        List<KartuIdentitas> latestRevision = revInfoService.findLatestRevision(KartuIdentitas.class, Long.valueOf(profileUpdate.getRevId()));
        if (latestRevision.isEmpty()) return;
        KartuIdentitas last = latestRevision.getLast();
        KartuIdentitas entity = repository.findById(Long.valueOf(profileUpdate.getRevId()))
                .orElseThrow(() -> new IllegalArgumentException("Unknown Kartu Identitas"));
        // Salin hanya id relasi dari entity audit (session Envers), re-attach via
        // getReferenceById di session saat ini — hindari proxy lintas session (bd kepegawaian-yu5j).
        entity.setBiodata(last.getBiodata() != null
                ? biodataRepository.getReferenceById(last.getBiodata().getNik())
                : null);
        entity.setJenisKartu(last.getJenisKartu() != null
                ? jenisKitasRepository.getReferenceById(last.getJenisKartu().getId())
                : null);
        entity.setNomorKartu(last.getNomorKartu());
        entity.setTanggalExpired(last.getTanggalExpired());
        entity.setTanggalTerima(last.getTanggalTerima());
        entity.setNotes(last.getNotes());
        entity.setChangedStatus(false);
        repository.save(entity);
    }
}
