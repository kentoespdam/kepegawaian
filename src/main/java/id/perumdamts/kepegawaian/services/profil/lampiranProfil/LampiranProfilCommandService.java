package id.perumdamts.kepegawaian.services.profil.lampiranProfil;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilAcceptRequest;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.profil.LampiranProfil;
import id.perumdamts.kepegawaian.mapper.profil.lampiranProfil.LampiranProfilMapper;
import id.perumdamts.kepegawaian.repositories.profil.jpa.LampiranProfilRepository;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import id.perumdamts.kepegawaian.utils.UploadResultUtil;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public SavedStatus<?> addLampiran(LampiranProfilPostRequest request) {
        try {
            boolean exists = repository.exists(request.getSpecification());
            if (exists)
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Lampiran Profil sudah ada");

            UploadResultUtil uploadedFile = fileUploadUtil.uploadFileSp(
                    request.getFileName(), request.getRef(),
                    String.valueOf(request.getRefId()));
            if (!uploadedFile.isSuccess())
                return SavedStatus.build(ESaveStatus.FAILED, uploadedFile.getMessage());

            LampiranProfil entity = LampiranProfilMapper.toEntity(
                    request,
                    uploadedFile.getFileName(),
                    uploadedFile.getHashedFileName(),
                    uploadedFile.getMimeType()
            );
            repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    public boolean deleteById(Long id) {
        Optional<LampiranProfil> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }

    @Transactional
    public SavedStatus<?> acceptLampiran(LampiranProfilAcceptRequest request, String oleh) {
        try {
            LampiranProfil lampiranProfil = repository.findOne(request.getSpecification())
                    .orElseThrow(() -> new RuntimeException("Lampiran Profil accept not found!"));
            LampiranProfil entity = LampiranProfilMapper.accept(lampiranProfil, oleh);
            repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
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
