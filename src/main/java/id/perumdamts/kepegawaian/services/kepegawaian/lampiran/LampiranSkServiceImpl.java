package id.perumdamts.kepegawaian.services.kepegawaian.lampiran;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkAcceptRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.lampiran.LampiranSkResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.penggajian.LampiranSk;
import id.perumdamts.kepegawaian.repositories.kepegawaian.LampiranSkRepository;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
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
public class LampiranSkServiceImpl implements LampiranSkService {
    private final LampiranSkRepository repository;
    private final FileUploadUtil fileUploadUtil;

    @Override
    public List<LampiranSkResponse> getLampiran(EJenisSk jenisSk, Long id) {
        return repository.findByRefAndRefId(jenisSk, id).stream()
                .map(LampiranSkResponse::from).toList();
    }

    @Override
    public LampiranSkResponse getLampiranById(Long id) {
        return repository.findById(id).map(LampiranSkResponse::from).orElse(null);
    }

    @Override
    public ResponseEntity<?> getFileLampiranById(EJenisSk jenisSk, Long id) {
        LampiranSk lampiranById = repository.findById(id).orElse(null);
        if (Objects.isNull(lampiranById))
            return ErrorResult.build("File Not Found");
        try {
            Path path = fileUploadUtil.generatePath(jenisSk, String.valueOf(lampiranById.getRefId()), lampiranById.getHashedFileName());
            FileInputStream stream = new FileInputStream(path.toFile());
            ByteArrayResource resource = new ByteArrayResource(stream.readAllBytes());
            stream.close();
            return ResponseEntity.ok()
                    .contentLength(resource.contentLength())
                    .header("Content-Type", lampiranById.getMimeType())
                    .header("Content-Disposition", "inline; filename=\"" + lampiranById.getFileName() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ErrorResult.build("File Not Found");
        }
    }

    @Transactional
    @Override
    public SavedStatus<?> addLampiran(LampiranSkPostRequest request) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists)
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Lampiran SK sudah ada");
        UploadResultUtil uploadedFile = fileUploadUtil.uploadFileSp(request.getFileName(), request.getRef(), String.valueOf(request.getRefId()));
        if (!uploadedFile.isSuccess())
            return SavedStatus.build(ESaveStatus.FAILED, uploadedFile.getMessage());
        LampiranSk entity = LampiranSkPostRequest.toEntity(request, uploadedFile.getFileName(), uploadedFile.getHashedFileName(), uploadedFile.getMimeType());
        LampiranSk save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, LampiranSkResponse.from(save));
    }

    @Transactional
    @Override
    public boolean deleteById(Long id) {
        boolean exists = repository.existsById(id);
        if (!exists)
            return false;
        repository.deleteById(id);
        return true;
    }

    @Override
    public boolean deleteLampiran(EJenisSk ref, Long refId, Long id) {
        Specification<LampiranSk> specification = (root, query, cb) ->
                cb.and(
                        cb.equal(root.get("ref"), ref),
                        cb.equal(root.get("refId"), refId),
                        cb.equal(root.get("id"), id)
                );
        boolean exists = repository.exists(specification);
        if (!exists)
            return false;
        repository.deleteById(id);
        return true;
    }

    @Transactional
    @Override
    public SavedStatus<?> acceptLampiran(LampiranSkAcceptRequest request, String oleh) {
        Optional<LampiranSk> one = repository.findOne(request.getSpecification());
        if (one.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Lampiran SK Not Found");
        LampiranSk entity = LampiranSkAcceptRequest.toEntity(one.get(), oleh);
        LampiranSk save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, LampiranSkResponse.from(save));
    }

    @Override
    public void deleteByRefId(Long id) {
        List<LampiranSk> list = repository.findAllByRefId(id)
                .stream().peek(lampiranSk -> lampiranSk.setIsDeleted(true))
                .toList();
        repository.saveAll(list);
    }
}
