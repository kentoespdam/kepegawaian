package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenjangPendidikanRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.BiodataRepository;
import id.perumdamts.kepegawaian.services.revInfo.RevInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BiodataProfileUpdateStrategy implements ProfileUpdateStrategy {
    private final RevInfoService revInfoService;
    private final BiodataRepository repository;
    private final JenjangPendidikanRepository jenjangPendidikanRepository;

    @Override
    public EProfileUpdateTable table() {
        return EProfileUpdateTable.BIODATA;
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
    public void resetEntityState(String revId) {
        repository.findAnyByNik(revId)
                .ifPresent(entity -> {
                    entity.setChangedStatus(false);
                    entity.setIsDeleted(false);
                    repository.save(entity);
                });
    }

    @Override
    public void handleRejectedInsert(String revId) {
        repository.deleteById(revId);
    }

    @Override
    public void revertToPreviousRevision(ProfileUpdate profileUpdate) {
        String revId = profileUpdate.getRevId();
        List<Biodata> latestRevision = revInfoService.findLatestRevision(Biodata.class, revId);
        if (latestRevision.isEmpty()) return;
        Biodata last = latestRevision.getLast();
        Biodata entity = repository.findById(revId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown Biodata"));
        entity.setNama(last.getNama());
        entity.setJenisKelamin(last.getJenisKelamin());
        entity.setTempatLahir(last.getTempatLahir());
        entity.setTanggalLahir(last.getTanggalLahir());
        entity.setAgama(last.getAgama());
        entity.setStatusKawin(last.getStatusKawin());
        entity.setAlamat(last.getAlamat());
        entity.setTelp(last.getTelp());
        entity.setIbuKandung(last.getIbuKandung());
        // Salin hanya id relasi dari entity audit (session Envers), re-attach via
        // getReferenceById di session saat ini — hindari proxy lintas session (bd kepegawaian-yu5j).
        entity.setPendidikanTerakhir(last.getPendidikanTerakhir() != null
                ? jenjangPendidikanRepository.getReferenceById(last.getPendidikanTerakhir().getId())
                : null);
        entity.setChangedStatus(false);
        repository.save(entity);
    }
}
