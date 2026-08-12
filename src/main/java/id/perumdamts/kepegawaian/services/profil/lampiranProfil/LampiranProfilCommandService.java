package id.perumdamts.kepegawaian.services.profil.lampiranProfil;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.profil.LampiranProfil;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.mapper.profil.lampiranProfil.LampiranProfilMapper;
import id.perumdamts.kepegawaian.repositories.profil.jpa.LampiranProfilRepository;
import id.perumdamts.kepegawaian.services.profil.profilUpdate.ProfileUpdateService;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import id.perumdamts.kepegawaian.utils.UploadResultUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LampiranProfilCommandService {
    private final LampiranProfilRepository repository;
    private final FileUploadUtil fileUploadUtil;
    private final ProfileUpdateService profileUpdateService;

    /**
     * ADR-0036 §6 + ADR-0038: lampiran masuk antrian tanpa kolom changed_status — guard enqueue
     * via konteks endpoint (self-service → disetujui=false + entri PENDING; admin → langsung stabil).
     */
    @Transactional
    public SavedStatus<Long> addLampiran(LampiranProfilPostRequest request, boolean requiresApproval) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists)
            throw new ConflictException("Lampiran Profil sudah ada");

        UploadResultUtil uploadedFile = fileUploadUtil.uploadFileSp(
                request.getFileName(), request.getRef(),
                String.valueOf(request.getRefId()));
        if (!uploadedFile.isSuccess())
            throw new RuntimeException(uploadedFile.getMessage());

        LampiranProfil entity = LampiranProfilMapper.toEntity(
                request,
                uploadedFile.getFileName(),
                uploadedFile.getHashedFileName(),
                uploadedFile.getMimeType()
        );
        if (requiresApproval) {
            entity.setDisetujui(false);
        }
        LampiranProfil saved = repository.save(entity);
        if (requiresApproval) {
            profileUpdateService.create(String.valueOf(saved.getId()),
                    RevisionMetadata.RevisionType.INSERT, EProfileUpdateTable.LAMPIRAN);
        }
        return SavedStatus.build(ESaveStatus.SUCCESS, saved.getId());
    }

    @Transactional
    public boolean deleteById(Long id, boolean requiresApproval) {
        Optional<LampiranProfil> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        if (requiresApproval) {
            profileUpdateService.create(String.valueOf(id),
                    RevisionMetadata.RevisionType.DELETE, EProfileUpdateTable.LAMPIRAN);
        }
        return true;
    }

    @Transactional
    public void deleteByRefId(EJenisLampiranProfil eJenisLampiranProfil, Long id) {
        Specification<LampiranProfil> specification = SpecificationBuilder.<LampiranProfil>of()
                .addEqual(eJenisLampiranProfil, "ref")
                .addEqual(id, "refId")
                .build();
        List<LampiranProfil> all = repository.findAll(specification).stream()
                .peek(lampiranProfil -> lampiranProfil.setIsDeleted(true)).toList();
        repository.saveAll(all);
    }
}
