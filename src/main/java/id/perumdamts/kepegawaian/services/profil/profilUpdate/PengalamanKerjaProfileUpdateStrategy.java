package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.profil.PengalamanKerja;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import id.perumdamts.kepegawaian.repositories.profil.jpa.BiodataRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.PengalamanKerjaRepository;
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
public class PengalamanKerjaProfileUpdateStrategy implements ProfileUpdateStrategy {
    private final RevInfoService revInfoService;
    private final PengalamanKerjaRepository repository;
    private final ChangedStatusResolver resolver;
    private final BiodataRepository biodataRepository;

    @Override
    public EProfileUpdateTable table() {
        return EProfileUpdateTable.PENGALAMAN_KERJA;
    }

    @Override
    public void markAsStable(String revId) {
        log.info("mark PengalamanKerja as Stable executed");
        repository.findById(Long.valueOf(revId))
                .ifPresent(pengalamanKerja -> {
                    pengalamanKerja.setChangedStatus(false);
                    pengalamanKerja.setDisetujui(true);
                    pengalamanKerja.setTanggalDisetujui(LocalDateTime.now());
                    pengalamanKerja.setDisetujuiOleh(resolver.currentUserId());
                    repository.save(pengalamanKerja);
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
        List<PengalamanKerja> latestRevision = revInfoService.findLatestRevision(PengalamanKerja.class, Long.valueOf(profileUpdate.getRevId()));
        if (latestRevision.isEmpty()) return;
        PengalamanKerja last = latestRevision.getLast();
        PengalamanKerja entity = repository.findById(Long.valueOf(profileUpdate.getRevId()))
                .orElseThrow(() -> new IllegalArgumentException("Unknown Pengalaman Kerja"));
        // Salin hanya id relasi dari entity audit (session Envers), re-attach via
        // getReferenceById di session saat ini — hindari proxy lintas session (bd kepegawaian-yu5j).
        entity.setBiodata(last.getBiodata() != null
                ? biodataRepository.getReferenceById(last.getBiodata().getNik())
                : null);
        entity.setNamaPerusahaan(last.getNamaPerusahaan());
        entity.setTypePerusahaan(last.getTypePerusahaan());
        entity.setJabatan(last.getJabatan());
        entity.setLokasi(last.getLokasi());
        entity.setTahunMasuk(last.getTahunMasuk());
        entity.setTahunKeluar(last.getTahunKeluar());
        entity.setNotes(last.getNotes());
        entity.setDisetujui(last.getDisetujui());
        entity.setTanggalPengajuan(last.getTanggalPengajuan());
        entity.setTanggalDisetujui(last.getTanggalDisetujui());
        entity.setDisetujuiOleh(last.getDisetujuiOleh());
        entity.setChangedStatus(false);
        repository.save(entity);
    }
}
