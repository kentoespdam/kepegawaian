package id.perumdamts.kepegawaian.services.profil.lampiranProfil;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilAcceptRequest;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilPostRequest;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.profil.LampiranProfil;
import id.perumdamts.kepegawaian.repositories.profil.LampiranProfilRepository;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import id.perumdamts.kepegawaian.utils.UploadResultUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LampiranProfilServiceImpl implements LampiranProfilService {
    private final LampiranProfilRepository repository;
    private final FileUploadUtil fileUploadUtil;

    @Override
    public List<LampiranProfilResponse> getLampiran(EJenisLampiranProfil eJenisLampiranProfil, Long id) {
        return repository.findByRefAndRefId(eJenisLampiranProfil, id).stream()
                .map(LampiranProfilResponse::from).toList();
    }

    @Override
    public LampiranProfilResponse getLampiranById(Long id) {
        return repository.findById(id)
                .map(LampiranProfilResponse::from).orElse(null);
    }

    @Override
    public ResponseEntity<?> getFileLampiranById(EJenisLampiranProfil ref, Long id) {
        LampiranProfil lampiranProfil = repository.findById(id).orElse(null);
        if (Objects.isNull(lampiranProfil))
            return ErrorResult.build("File Not Found!");
        try {
            Path path = fileUploadUtil.generatePath(ref, String.valueOf(lampiranProfil.getRefId()), lampiranProfil.getHashedFileName());
            FileInputStream stream = new FileInputStream(path.toFile());
            ByteArrayResource resource = new ByteArrayResource(stream.readAllBytes());
            stream.close();
            return ResponseEntity.ok()
                    .contentLength(resource.contentLength())
                    .header("Content-Type", lampiranProfil.getMimeType())
                    .header("Content-Disposition", "inline; filename=\"" + lampiranProfil.getFileName() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ErrorResult.build("File Not Found!");
        }
    }

    @Transactional
    @Override
    public SavedStatus<?> addLampiran(LampiranProfilPostRequest request) {
        try {
            boolean exists = repository.exists(request.getSpecification());
            if (exists)
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Lampiran Profil sudah ada");

            UploadResultUtil uploadedFile = fileUploadUtil.uploadFileSp(request.getFileName(), request.getRef(), String.valueOf(request.getRefId()));
            if (!uploadedFile.isSuccess())
                return SavedStatus.build(ESaveStatus.FAILED, uploadedFile.getMessage());

            LampiranProfil entity = LampiranProfilPostRequest.toEntity(
                    request,
                    uploadedFile.getFileName(),
                    uploadedFile.getHashedFileName(),
                    uploadedFile.getMimeType()
            );
            repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Lampiran Profil Saved");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    @Override
    public boolean deleteById(Long id) {
        Optional<LampiranProfil> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }

    @Transactional
    @Override
    public SavedStatus<?> acceptLampiran(LampiranProfilAcceptRequest request, String oleh) {
        try {
            LampiranProfil lampiranProfil = repository.findOne(request.getSpecification())
                    .orElseThrow(() -> new RuntimeException("Lampiran Profil accept not found!"));
            LampiranProfil entity = LampiranProfilAcceptRequest.toEntity(lampiranProfil, oleh);
            repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Success Accept Lampiran");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    @Override
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
