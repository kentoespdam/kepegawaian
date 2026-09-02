package id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootProcessRequest;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchRootRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Regression test: Workflow status transitions must work with ordinal-based
 * EProsesGaji enum (INTEGER DB column).
 */
@ExtendWith(MockitoExtension.class)
class GajiBatchRootWorkflowCommandServiceTest {

    private static final String BATCH_ID = "202609-001";

    @Mock
    private GajiBatchRootRepository repository;
    @Mock
    private GajiBatchRootEventPublisher eventPublisher;

    private GajiBatchRootWorkflowCommandService workflowService;

    @BeforeEach
    void setUp() {
        workflowService = new GajiBatchRootWorkflowCommandService(repository, eventPublisher);
    }

    private GajiBatchRoot buildBatch(EProsesGaji status) {
        GajiBatchRoot entity = new GajiBatchRoot();
        entity.setId(BATCH_ID);
        entity.setPeriode("202609");
        entity.setStatus(status);
        return entity;
    }

    private GajiBatchRootProcessRequest buildRequest() {
        GajiBatchRootProcessRequest request = new GajiBatchRootProcessRequest();
        request.setId(BATCH_ID);
        request.setNama("Verifier");
        request.setJabatan("Kabag");
        return request;
    }

    // --- VERIFY 1 ---

    @Test
    void verify1_fromPending_transitionsToWaitVerificationPhase2() {
        GajiBatchRoot entity = buildBatch(EProsesGaji.PENDING);
        when(repository.findById(BATCH_ID)).thenReturn(Optional.of(entity));
        when(repository.save(any(GajiBatchRoot.class))).thenReturn(entity);

        GajiBatchRootProcessRequest request = buildRequest();
        SavedStatus<String> result = workflowService.verify1(request);

        assertEquals(ESaveStatus.SUCCESS, result.getStatus());
        verify(repository).save(argThat(e ->
                ((GajiBatchRoot) e).getStatus() == EProsesGaji.WAIT_VERIFICATION_PHASE_2
        ));
    }

    @Test
    void verify1_setsVerificationFields() {
        GajiBatchRoot entity = buildBatch(EProsesGaji.PENDING);
        when(repository.findById(BATCH_ID)).thenReturn(Optional.of(entity));
        when(repository.save(any(GajiBatchRoot.class))).thenReturn(entity);

        GajiBatchRootProcessRequest request = buildRequest();
        workflowService.verify1(request);

        verify(repository).save(argThat(e -> {
            GajiBatchRoot root = (GajiBatchRoot) e;
            return root.getTanggalVerifikasiTahap1() != null
                    && "Verifier".equals(root.getDiVerifikasiOlehTahap1())
                    && "Kabag".equals(root.getJabatanVerifikasiTahap1());
        }));
    }

    @Test
    void verify1_batchNotFound_throwsNotFoundException() {
        when(repository.findById("nonexistent")).thenReturn(Optional.empty());

        GajiBatchRootProcessRequest request = buildRequest();
        request.setId("nonexistent");

        assertThrows(NotFoundException.class, () -> workflowService.verify1(request));
    }

    // --- VERIFY 2 ---

    @Test
    void verify2_fromWaitVerificationPhase2_transitionsToWaitApproval() {
        GajiBatchRoot entity = buildBatch(EProsesGaji.WAIT_VERIFICATION_PHASE_2);
        when(repository.findById(BATCH_ID)).thenReturn(Optional.of(entity));
        when(repository.save(any(GajiBatchRoot.class))).thenReturn(entity);

        GajiBatchRootProcessRequest request = buildRequest();
        SavedStatus<String> result = workflowService.verify2(request);

        assertEquals(ESaveStatus.SUCCESS, result.getStatus());
        verify(repository).save(argThat(e ->
                ((GajiBatchRoot) e).getStatus() == EProsesGaji.WAIT_APPROVAL
        ));
    }

    @Test
    void verify2_setsVerification2Fields() {
        GajiBatchRoot entity = buildBatch(EProsesGaji.WAIT_VERIFICATION_PHASE_2);
        when(repository.findById(BATCH_ID)).thenReturn(Optional.of(entity));
        when(repository.save(any(GajiBatchRoot.class))).thenReturn(entity);

        GajiBatchRootProcessRequest request = buildRequest();
        workflowService.verify2(request);

        verify(repository).save(argThat(e -> {
            GajiBatchRoot root = (GajiBatchRoot) e;
            return root.getTanggalVerifikasiTahap2() != null
                    && "Verifier".equals(root.getDiVerifikasiOlehTahap2())
                    && "Kabag".equals(root.getJabatanVerifikasiTahap2());
        }));
    }

    // --- ACCEPT ---

    @Test
    void accept_fromWaitApproval_transitionsToFinished() {
        GajiBatchRoot entity = buildBatch(EProsesGaji.WAIT_APPROVAL);
        when(repository.findById(BATCH_ID)).thenReturn(Optional.of(entity));
        when(repository.save(any(GajiBatchRoot.class))).thenReturn(entity);

        GajiBatchRootProcessRequest request = buildRequest();
        SavedStatus<String> result = workflowService.accept(request);

        assertEquals(ESaveStatus.SUCCESS, result.getStatus());
        verify(repository).save(argThat(e ->
                ((GajiBatchRoot) e).getStatus() == EProsesGaji.FINISHED
        ));
    }

    @Test
    void accept_setsApprovalFields() {
        GajiBatchRoot entity = buildBatch(EProsesGaji.WAIT_APPROVAL);
        when(repository.findById(BATCH_ID)).thenReturn(Optional.of(entity));
        when(repository.save(any(GajiBatchRoot.class))).thenReturn(entity);

        GajiBatchRootProcessRequest request = buildRequest();
        workflowService.accept(request);

        verify(repository).save(argThat(e -> {
            GajiBatchRoot root = (GajiBatchRoot) e;
            return root.getTanggalPersetujuan() != null
                    && "Verifier".equals(root.getDiSetujuiOleh())
                    && "Kabag".equals(root.getJabatanPenyetuju());
        }));
    }

    @Test
    void accept_batchNotFound_throwsNotFoundException() {
        when(repository.findById("nonexistent")).thenReturn(Optional.empty());

        GajiBatchRootProcessRequest request = buildRequest();
        request.setId("nonexistent");

        assertThrows(NotFoundException.class, () -> workflowService.accept(request));
    }

    // --- REPROCESS ---

    @Test
    void reprocess_fromWaitApproval_transitionsBackToWaitVerificationPhase2() {
        GajiBatchRoot entity = buildBatch(EProsesGaji.WAIT_APPROVAL);
        when(repository.findById(BATCH_ID)).thenReturn(Optional.of(entity));
        when(repository.save(any(GajiBatchRoot.class))).thenReturn(entity);

        GajiBatchRootProcessRequest request = buildRequest();
        request.setPhase(EProsesGaji.WAIT_APPROVAL);
        SavedStatus<String> result = workflowService.reprocess(request);

        assertEquals(ESaveStatus.SUCCESS, result.getStatus());
        verify(repository).save(argThat(e ->
                ((GajiBatchRoot) e).getStatus() == EProsesGaji.WAIT_VERIFICATION_PHASE_2
        ));
    }

    @Test
    void reprocess_fromWaitVerificationPhase2_transitionsToWaitVerificationPhase1() {
        GajiBatchRoot entity = buildBatch(EProsesGaji.WAIT_VERIFICATION_PHASE_2);
        when(repository.findById(BATCH_ID)).thenReturn(Optional.of(entity));
        when(repository.save(any(GajiBatchRoot.class))).thenReturn(entity);

        GajiBatchRootProcessRequest request = buildRequest();
        request.setPhase(EProsesGaji.WAIT_VERIFICATION_PHASE_2);
        workflowService.reprocess(request);

        verify(repository).save(argThat(e ->
                ((GajiBatchRoot) e).getStatus() == EProsesGaji.WAIT_VERIFICATION_PHASE_1
        ));
    }

    @Test
    void reprocess_transitionToPending_publishesEvent() {
        GajiBatchRoot entity = buildBatch(EProsesGaji.WAIT_VERIFICATION_PHASE_1);
        when(repository.findById(BATCH_ID)).thenReturn(Optional.of(entity));
        when(repository.save(any(GajiBatchRoot.class))).thenReturn(entity);

        GajiBatchRootProcessRequest request = buildRequest();
        request.setPhase(EProsesGaji.WAIT_VERIFICATION_PHASE_1);
        workflowService.reprocess(request);

        verify(eventPublisher).publishAfterCommit(BATCH_ID);
    }

    @Test
    void reprocess_fromWaitVerificationPhase1_transitionsToPending() {
        GajiBatchRoot entity = buildBatch(EProsesGaji.WAIT_VERIFICATION_PHASE_1);
        when(repository.findById(BATCH_ID)).thenReturn(Optional.of(entity));
        when(repository.save(any(GajiBatchRoot.class))).thenReturn(entity);

        GajiBatchRootProcessRequest request = buildRequest();
        request.setPhase(EProsesGaji.WAIT_VERIFICATION_PHASE_1);
        workflowService.reprocess(request);

        verify(repository).save(argThat(e ->
                ((GajiBatchRoot) e).getStatus() == EProsesGaji.PENDING
        ));
    }

    // --- Full workflow status transition chain ---

    @Test
    void fullWorkflow_pendingToFinished() {
        GajiBatchRoot entity = buildBatch(EProsesGaji.PENDING);

        // Step 1: PENDING → WAIT_VERIFICATION_PHASE_2
        when(repository.findById(BATCH_ID)).thenReturn(Optional.of(entity));
        when(repository.save(any(GajiBatchRoot.class))).thenReturn(entity);
        workflowService.verify1(buildRequest());
        assertEquals(EProsesGaji.WAIT_VERIFICATION_PHASE_2, entity.getStatus(),
                "After verify1, status must be WAIT_VERIFICATION_PHASE_2");

        // Step 2: WAIT_VERIFICATION_PHASE_2 → WAIT_APPROVAL
        when(repository.findById(BATCH_ID)).thenReturn(Optional.of(entity));
        when(repository.save(any(GajiBatchRoot.class))).thenReturn(entity);
        workflowService.verify2(buildRequest());
        assertEquals(EProsesGaji.WAIT_APPROVAL, entity.getStatus(),
                "After verify2, status must be WAIT_APPROVAL");

        // Step 3: WAIT_APPROVAL → FINISHED
        when(repository.findById(BATCH_ID)).thenReturn(Optional.of(entity));
        when(repository.save(any(GajiBatchRoot.class))).thenReturn(entity);
        workflowService.accept(buildRequest());
        assertEquals(EProsesGaji.FINISHED, entity.getStatus(),
                "After accept, status must be FINISHED");

        // Verify total save count
        verify(repository, times(3)).save(any(GajiBatchRoot.class));
    }
}
