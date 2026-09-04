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
        reprocessHandler(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, "success");
    }

    @Transactional
    public SavedStatus<String> verify(GajiBatchRootProcessRequest request) {
        GajiBatchRoot entity = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Unknown Batch Process"));
        verifyHandler(entity, request);
        return SavedStatus.build(ESaveStatus.SUCCESS, "success");
    }

    private void verifyHandler(GajiBatchRoot entity, GajiBatchRootProcessRequest request) {
        switch (entity.getStatus()) {
            case WAIT_VERIFICATION_PHASE_1 -> {
                entity.setStatus(EProsesGaji.WAIT_VERIFICATION_PHASE_2);
                entity.setTanggalVerifikasiTahap1(LocalDateTime.now());
                entity.setDiVerifikasiOlehTahap1(request.getNama());
                entity.setJabatanVerifikasiTahap1(request.getJabatan());
            }
            case WAIT_VERIFICATION_PHASE_2 -> {
                entity.setStatus(EProsesGaji.WAIT_APPROVAL);
                entity.setTanggalVerifikasiTahap2(LocalDateTime.now());
                entity.setDiVerifikasiOlehTahap2(request.getNama());
                entity.setJabatanVerifikasiTahap2(request.getJabatan());
            }
            case WAIT_APPROVAL -> {
                entity.setStatus(EProsesGaji.FINISHED);
                entity.setTanggalPersetujuan(LocalDateTime.now());
                entity.setDiSetujuiOleh(request.getNama());
                entity.setJabatanPenyetuju(request.getJabatan());
            }
        }
        repository.save(entity);
    }

    private void reprocessHandler(GajiBatchRoot entity) {
        switch (entity.getStatus()) {
            case WAIT_APPROVAL -> entity.setStatus(EProsesGaji.WAIT_VERIFICATION_PHASE_2);
            case WAIT_VERIFICATION_PHASE_2 -> entity.setStatus(EProsesGaji.WAIT_VERIFICATION_PHASE_1);
            case WAIT_VERIFICATION_PHASE_1 -> entity.setStatus(EProsesGaji.PENDING);
            case FAILED -> entity.setStatus(EProsesGaji.PENDING);
        }
        GajiBatchRoot save = repository.save(entity);
        if (save.getStatus() == EProsesGaji.PENDING) {
            eventPublisher.publishAfterCommit(save.getId());
        }
    }
}
