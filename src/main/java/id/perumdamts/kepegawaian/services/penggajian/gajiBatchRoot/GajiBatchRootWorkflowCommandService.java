package id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootProcessRequest;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchRootRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class GajiBatchRootWorkflowCommandService {
    private final GajiBatchRootRepository repository;
    private final GajiBatchRootEventPublisher eventPublisher;

    @Transactional
    public SavedStatus<String> reprocess(GajiBatchRootProcessRequest request) {
        GajiBatchRoot entity = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Unknown Batch Process"));
        reprocessHandler(entity, request);
        return SavedStatus.build(ESaveStatus.SUCCESS, "success");
    }

    @Transactional
    public SavedStatus<String> verify1(GajiBatchRootProcessRequest request) {
        GajiBatchRoot gajiBatchRoot = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Unknown Batch Process"));
        GajiBatchRoot verified = GajiBatchRootProcessRequest.verifyPhase1(gajiBatchRoot, request);
        repository.save(verified);
        return SavedStatus.build(ESaveStatus.SUCCESS, "success");
    }

    @Transactional
    public SavedStatus<String> verify2(GajiBatchRootProcessRequest request) {
        GajiBatchRoot gajiBatchRoot = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Unknown Batch Process"));
        GajiBatchRoot verified = GajiBatchRootProcessRequest.verifyPhase2(gajiBatchRoot, request);
        repository.save(verified);
        return SavedStatus.build(ESaveStatus.SUCCESS, "success");
    }

    @Transactional
    public SavedStatus<String> accept(GajiBatchRootProcessRequest request) {
        GajiBatchRoot gajiBatchRoot = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Unknown Batch Process"));
        GajiBatchRoot accepted = GajiBatchRootProcessRequest.accept(gajiBatchRoot, request);
        repository.save(accepted);
        return SavedStatus.build(ESaveStatus.SUCCESS, "success");
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
}
