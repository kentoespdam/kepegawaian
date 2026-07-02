package id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootProcessRequest;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchRootRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GajiBatchRootWorkflowCommandService {
    private final GajiBatchRootRepository repository;
    private final GajiBatchRootEventPublisher eventPublisher;

    @Transactional
    public SavedStatus<?> reprocess(GajiBatchRootProcessRequest request) {
        try {
            GajiBatchRoot entity = repository.findById(request.getId())
                    .orElseThrow(() -> new RuntimeException("Unknown Batch Process"));

            reprocessHandler(entity, request);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Reprocess Penggajian Executed");
        } catch (RuntimeException e) {
            if ("Unknown Batch Process".equals(e.getMessage())) {
                return SavedStatus.build(ESaveStatus.FAILED, "Unknown Batch Process");
            }
            return logAndBuildFailure("reprocess", e);
        }
    }

    @Transactional
    public SavedStatus<?> verify1(String id, GajiBatchRootProcessRequest request) {
        try {
            Optional<GajiBatchRoot> byId = repository.findById(request.getId());
            if (byId.isEmpty())
                return SavedStatus.build(ESaveStatus.FAILED, "Unknown Batch Process");
            GajiBatchRoot gajiBatchRoot = GajiBatchRootProcessRequest.verifyPhase1(byId.get(), request);
            repository.save(gajiBatchRoot);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Verifikasi Tahap 1 Saved");
        } catch (Exception e) {
            return logAndBuildFailure("verify1", e);
        }
    }

    @Transactional
    public SavedStatus<?> verify2(String id, GajiBatchRootProcessRequest request) {
        try {
            Optional<GajiBatchRoot> byId = repository.findById(request.getId());
            if (byId.isEmpty())
                return SavedStatus.build(ESaveStatus.FAILED, "Unknown Batch Process");
            GajiBatchRoot gajiBatchRoot = GajiBatchRootProcessRequest.verifyPhase2(byId.get(), request);
            repository.save(gajiBatchRoot);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Verifikasi Tahap 2 Saved");
        } catch (Exception e) {
            return logAndBuildFailure("verify2", e);
        }
    }

    @Transactional
    public SavedStatus<?> accept(String id, GajiBatchRootProcessRequest request) {
        try {
            Optional<GajiBatchRoot> byId = repository.findById(request.getId());
            if (byId.isEmpty())
                return SavedStatus.build(ESaveStatus.FAILED, "Unknown Batch Process");
            GajiBatchRoot gajiBatchRoot = GajiBatchRootProcessRequest.accept(byId.get(), request);
            repository.save(gajiBatchRoot);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Batch Accepted");
        } catch (Exception e) {
            return logAndBuildFailure("accept", e);
        }
    }

    private void reprocessHandler(GajiBatchRoot entity, GajiBatchRootProcessRequest request) {
        switch (request.getPhase()) {
            case WAIT_APPROVAL -> entity.setStatus(EProsesGaji.WAIT_VERIFICATION_PHASE_2);
            case WAIT_VERIFICATION_PHASE_2 -> entity.setStatus(EProsesGaji.WAIT_VERIFICATION_PHASE_1);
            case WAIT_VERIFICATION_PHASE_1 -> entity.setStatus(EProsesGaji.PENDING);
        }
        entity.setTanggalVerifikasiTahap1(LocalDateTime.now());
        entity.setDiVerifikasiOlehTahap1(request.getNama());
        entity.setJabatanVerifikasiTahap1(request.getJabatan());
        GajiBatchRoot save = repository.save(entity);
        if (save.getStatus() == EProsesGaji.PENDING) {
            eventPublisher.publishAfterCommit(save.getId());
        }
    }

    private SavedStatus<?> logAndBuildFailure(String operation, Exception e) {
        log.error("GajiBatchRoot {} failed", operation, e);
        return SavedStatus.build(ESaveStatus.FAILED, "Gaji Batch operation failed");
    }
}
