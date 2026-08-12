package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.profil.LampiranProfil;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import id.perumdamts.kepegawaian.repositories.profil.jpa.LampiranProfilRepository;
import id.perumdamts.kepegawaian.services.profil.ChangedStatusResolver;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class LampiranProfilProfileUpdateStrategy implements ProfileUpdateStrategy {
    private final LampiranProfilRepository repository;
    private final ChangedStatusResolver resolver;
    private final FileUploadUtil fileUploadUtil;

    @Override
    public EProfileUpdateTable table() {
        return EProfileUpdateTable.LAMPIRAN;
    }

    @Override
    public void markAsStable(String revId) {
        log.info("mark LampiranProfil as Stable executed");
        repository.findById(Long.valueOf(revId))
                .ifPresent(lampiranProfil -> {
                    lampiranProfil.setDisetujui(true);
                    lampiranProfil.setTanggalDisetujui(LocalDateTime.now());
                    lampiranProfil.setDisetujuiOleh(resolver.currentUserId());
                    repository.save(lampiranProfil);
                });
    }

    @Override
    public void resetEntityState(String id) {
        repository.findAnyById(Long.valueOf(id))
                .ifPresent(entity -> {
                    entity.setIsDeleted(false);
                    repository.save(entity);
                });
    }

    @Override
    public void handleRejectedInsert(String revId) {
        repository.findById(Long.valueOf(revId))
                .ifPresent(lampiranProfil -> {
                    fileUploadUtil.deleteOldFile(lampiranProfil.getHashedFileName(),
                            lampiranProfil.getRef(), String.valueOf(lampiranProfil.getRefId()));
                    repository.deleteById(lampiranProfil.getId());
                });
    }

    @Override
    public void revertToPreviousRevision(ProfileUpdate profileUpdate) {
        // LampiranProfil hanya INSERT/DELETE — tidak ada UPDATE (ADR-0036 §6).
        throw new IllegalStateException("LampiranProfil does not support UPDATE action");
    }
}
