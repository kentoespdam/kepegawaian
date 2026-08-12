package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.profil.Pelatihan;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import id.perumdamts.kepegawaian.repositories.profil.jpa.PelatihanRepository;
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
public class PelatihanProfileUpdateStrategy implements ProfileUpdateStrategy {
    private final RevInfoService revInfoService;
    private final PelatihanRepository repository;
    private final ChangedStatusResolver resolver;

    @Override
    public EProfileUpdateTable table() {
        return EProfileUpdateTable.PELATIHAN;
    }

    @Override
    public void markAsStable(String revId) {
        log.info("mark Pelatihan as Stable executed");
        repository.findById(Long.valueOf(revId))
                .ifPresent(pelatihan -> {
                    pelatihan.setChangedStatus(false);
                    pelatihan.setDisetujui(true);
                    pelatihan.setTanggalDisetujui(LocalDateTime.now());
                    pelatihan.setDisetujuiOleh(resolver.currentUserId());
                    repository.save(pelatihan);
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
        List<Pelatihan> latestRevision = revInfoService.findLatestRevision(Pelatihan.class, Long.valueOf(profileUpdate.getRevId()));
        if (latestRevision.isEmpty()) return;
        Pelatihan last = latestRevision.getLast();
        Pelatihan entity = repository.findById(Long.valueOf(profileUpdate.getRevId()))
                .orElseThrow(() -> new IllegalArgumentException("Unknown Pelatihan"));
        entity.setBiodata(last.getBiodata());
        entity.setJenisPelatihan(last.getJenisPelatihan());
        entity.setNama(last.getNama());
        entity.setLembaga(last.getLembaga());
        entity.setTanggalMulai(last.getTanggalMulai());
        entity.setTanggalSelesai(last.getTanggalSelesai());
        entity.setLulus(last.getLulus());
        entity.setNilai(last.getNilai());
        entity.setIkatanDinas(last.getIkatanDinas());
        entity.setTanggalAkhirIkatan(last.getTanggalAkhirIkatan());
        entity.setNotes(last.getNotes());
        entity.setDisetujui(last.getDisetujui());
        entity.setTanggalPengajuan(last.getTanggalPengajuan());
        entity.setTanggalDisetujui(last.getTanggalDisetujui());
        entity.setDisetujuiOleh(last.getDisetujuiOleh());
        entity.setChangedStatus(false);
        repository.save(entity);
    }
}
