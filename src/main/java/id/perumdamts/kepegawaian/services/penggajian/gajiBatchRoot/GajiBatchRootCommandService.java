package id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisPotonganGaji;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootLampiran;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

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
    public SavedStatus<String> save(GajiBatchRootPostRequest request) {
        Optional<GajiBatchRoot> byId = repository.findById(request.getPeriode());
        if (byId.isPresent())
            throw new ConflictException("Batch already exists");

        GajiBatchRoot entity = GajiBatchRootMapper.toEntityPhase1(request);
        repository.findDeletedBatchRoot(request.getPeriode())
                .ifPresent(gajiBatchRoot -> {
                    String nextBatchId = request.nextBatchId(gajiBatchRoot.getId());
                    entity.setId(nextBatchId);
                });

        GajiBatchRoot save = repository.save(entity);
        final String batchId = save.getId();
        final MultipartFile filePart = request.getFileName();
        final String periode = entity.getPeriode();

        if (filePart != null) {
            // #3 kepegawaian-f5i: upload di luar tx — orphans terjadi saat
            // rollback setelah file ter-upload tapi sebelum tx commit.
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    String subFolder = "PotonganTKK/" + periode;
                    try {
                        UploadResultUtil uploadResult = fileUploadUtil.uploadPenggajian(filePart, subFolder);
                        GajiBatchRootLampiran lampiran = new GajiBatchRootLampiran(
                                save,
                                EJenisPotonganGaji.POTONGAN_TKK,
                                uploadResult.getMimeType(),
                                uploadResult.getFileName(),
                                uploadResult.getHashedFileName());
                        gajiBatchRootLampiranRepository.save(lampiran);
                        // #4 kepegawaian-jgm: processPotonganTkk juga di afterCommit
                        processPotonganTkk.process(batchId);
                    } catch (RuntimeException ex) {
                        log.error("Failed post-commit processing for batch {}: {}", batchId, ex.getMessage(), ex);
                    }
                }
            });
        }

        eventPublisher.publishAfterCommit(batchId);
        return SavedStatus.build(ESaveStatus.SUCCESS, "1 success");
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
}
