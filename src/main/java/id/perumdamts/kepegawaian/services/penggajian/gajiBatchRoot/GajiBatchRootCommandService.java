package id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisPotonganGaji;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootLampiran;
import id.perumdamts.kepegawaian.mapper.penggajian.gajiBatchRoot.GajiBatchRootMapper;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchRootLampiranRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchRootRepository;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import id.perumdamts.kepegawaian.utils.ProcessPotonganTkk;
import id.perumdamts.kepegawaian.utils.UploadResultUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GajiBatchRootCommandService {
    private final GajiBatchRootRepository repository;
    private final FileUploadUtil fileUploadUtil;
    private final ProcessPotonganTkk processPotonganTkk;
    private final GajiBatchRootLampiranRepository gajiBatchRootLampiranRepository;
    private final GajiBatchRootEventPublisher eventPublisher;

    @Transactional
    public SavedStatus<?> save(GajiBatchRootPostRequest request) {
        try {
            Optional<GajiBatchRoot> byId = repository.findById(request.getPeriode());
            if (byId.isPresent())
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Batch already exists");

            GajiBatchRoot entity = GajiBatchRootMapper.toEntityPhase1(request);
            repository.findDeletedBatchRoot(request.getPeriode())
                    .ifPresent(gajiBatchRoot -> {
                        String nextBatchId = request.nextBatchId(gajiBatchRoot.getId());
                        entity.setId(nextBatchId);
                    });

            GajiBatchRoot save = repository.save(entity);

            if (request.getFileName() != null) {
                String subFolder = "PotonganTKK/" + entity.getPeriode();
                UploadResultUtil uploadResultUtil = fileUploadUtil.uploadPenggajian(
                        request.getFileName(),
                        subFolder
                );
                try {
                    GajiBatchRootLampiran gajiBatchRootLampiran = new GajiBatchRootLampiran(
                            entity,
                            EJenisPotonganGaji.POTONGAN_TKK,
                            uploadResultUtil.getMimeType(),
                            uploadResultUtil.getFileName(),
                            uploadResultUtil.getHashedFileName());
                    gajiBatchRootLampiranRepository.save(gajiBatchRootLampiran);
                    processPotonganTkk.process(entity.getId());
                } catch (RuntimeException postUploadEx) {
                    // Compensating action: drop the just-uploaded file so the
                    // rollback of the Lampiran row + potongan_tkk rows leaves
                    // no orphan on disk.
                    try {
                        fileUploadUtil.deleteOldFilePenggajian(
                                subFolder, uploadResultUtil.getHashedFileName());
                    } catch (RuntimeException cleanupEx) {
                        log.warn("Failed to delete orphaned upload {}/{}: {}",
                                subFolder, uploadResultUtil.getHashedFileName(),
                                cleanupEx.getMessage());
                    }
                    throw postUploadEx;
                }
            }
            final String batchId = save.getId();
            eventPublisher.publishAfterCommit(batchId);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Batch Gaji Saved");
        } catch (Exception e) {
            return logAndBuildFailure("save", e);
        }
    }

    @Transactional
    public boolean delete(String id) {
        Optional<GajiBatchRoot> byId = repository.findById(id);
        if (byId.isEmpty()
                || byId.get().getStatus() == EProsesGaji.PROSES
                || byId.get().getStatus() == EProsesGaji.FINISHED)
            return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }

    private SavedStatus<?> logAndBuildFailure(String operation, Exception e) {
        log.error("GajiBatchRoot {} failed", operation, e);
        return SavedStatus.build(ESaveStatus.FAILED, "Gaji Batch operation failed");
    }
}
