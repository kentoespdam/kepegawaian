package id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchRootLampiranRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchRootRepository;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import id.perumdamts.kepegawaian.utils.ProcessPotonganTkk;
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
 * Regression test: GajiBatchRootCommandService CRUD operations must handle
 * EProsesGaji status as ordinal (INTEGER) correctly when saving/deleting.
 */
@ExtendWith(MockitoExtension.class)
class GajiBatchRootCommandServiceTest {

    @Mock
    private GajiBatchRootRepository repository;
    @Mock
    private FileUploadUtil fileUploadUtil;
    @Mock
    private ProcessPotonganTkk processPotonganTkk;
    @Mock
    private GajiBatchRootLampiranRepository gajiBatchRootLampiranRepository;
    @Mock
    private GajiBatchRootEventPublisher eventPublisher;

    private GajiBatchRootCommandService commandService;

    @BeforeEach
    void setUp() {
        commandService = new GajiBatchRootCommandService(
                repository, fileUploadUtil, processPotonganTkk,
                gajiBatchRootLampiranRepository, eventPublisher);
    }

    // --- SAVE ---

    @Test
    void save_newBatch_setsStatusPENDING() {
        GajiBatchRootPostRequest request = new GajiBatchRootPostRequest();
        request.setTahun("2026");
        request.setBulan("09");
        request.setDiProsesOleh("Operator");

        when(repository.findById("202609")).thenReturn(Optional.empty());
        when(repository.findDeletedBatchRoot("202609")).thenReturn(Optional.empty());

        GajiBatchRoot savedEntity = new GajiBatchRoot();
        savedEntity.setId("202609-001");
        savedEntity.setPeriode("202609");
        savedEntity.setStatus(EProsesGaji.PENDING);
        when(repository.save(any(GajiBatchRoot.class))).thenReturn(savedEntity);

        SavedStatus<String> result = commandService.save(request);

        assertEquals(ESaveStatus.SUCCESS, result.getStatus());
        verify(repository).save(argThat(entity ->
                entity.getStatus() == EProsesGaji.PENDING
        ));
    }

    @Test
    void save_duplicatePeriod_throwsConflict() {
        GajiBatchRootPostRequest request = new GajiBatchRootPostRequest();
        request.setTahun("2026");
        request.setBulan("09");

        GajiBatchRoot existing = new GajiBatchRoot();
        existing.setId("202609-001");
        existing.setStatus(EProsesGaji.PENDING);
        when(repository.findById("202609")).thenReturn(Optional.of(existing));

        assertThrows(ConflictException.class, () -> commandService.save(request));
    }

    @Test
    void save_deletedBatchExists_generatesNextBatchId() {
        GajiBatchRootPostRequest request = new GajiBatchRootPostRequest();
        request.setTahun("2026");
        request.setBulan("09");

        when(repository.findById("202609")).thenReturn(Optional.empty());

        GajiBatchRoot deletedBatch = new GajiBatchRoot();
        deletedBatch.setId("202609-001");
        when(repository.findDeletedBatchRoot("202609")).thenReturn(Optional.of(deletedBatch));

        GajiBatchRoot savedEntity = new GajiBatchRoot();
        savedEntity.setId("202609-002");
        when(repository.save(any(GajiBatchRoot.class))).thenReturn(savedEntity);

        SavedStatus<String> result = commandService.save(request);

        assertEquals(ESaveStatus.SUCCESS, result.getStatus());
        verify(repository).save(argThat(entity ->
                "202609-002".equals(entity.getId())
        ));
    }

    @Test
    void save_publishesEventAfterSaving() {
        GajiBatchRootPostRequest request = new GajiBatchRootPostRequest();
        request.setTahun("2026");
        request.setBulan("09");

        when(repository.findById("202609")).thenReturn(Optional.empty());
        when(repository.findDeletedBatchRoot("202609")).thenReturn(Optional.empty());

        GajiBatchRoot savedEntity = new GajiBatchRoot();
        savedEntity.setId("202609-001");
        when(repository.save(any(GajiBatchRoot.class))).thenReturn(savedEntity);

        commandService.save(request);

        verify(eventPublisher).publishAfterCommit("202609-001");
    }

    // --- DELETE ---

    @Test
    void delete_pendingBatch_returnsTrue() {
        GajiBatchRoot entity = new GajiBatchRoot();
        entity.setId("202609-001");
        entity.setStatus(EProsesGaji.PENDING);
        when(repository.findById("202609-001")).thenReturn(Optional.of(entity));
        when(repository.save(any(GajiBatchRoot.class))).thenReturn(entity);

        boolean result = commandService.delete("202609-001");

        assertTrue(result, "Delete should succeed for PENDING batch");
        assertTrue(entity.getIsDeleted(), "isDeleted must be set to true");
    }

    @Test
    void delete_prosesBatch_returnsFalse() {
        GajiBatchRoot entity = new GajiBatchRoot();
        entity.setId("202609-001");
        entity.setStatus(EProsesGaji.PROSES);
        when(repository.findById("202609-001")).thenReturn(Optional.of(entity));

        boolean result = commandService.delete("202609-001");

        assertFalse(result, "Delete must fail for PROSES batch");
    }

    @Test
    void delete_finishedBatch_returnsFalse() {
        GajiBatchRoot entity = new GajiBatchRoot();
        entity.setId("202609-001");
        entity.setStatus(EProsesGaji.FINISHED);
        when(repository.findById("202609-001")).thenReturn(Optional.of(entity));

        boolean result = commandService.delete("202609-001");

        assertFalse(result, "Delete must fail for FINISHED batch");
    }

    @Test
    void delete_nonExistentBatch_returnsFalse() {
        when(repository.findById("nonexistent")).thenReturn(Optional.empty());

        boolean result = commandService.delete("nonexistent");

        assertFalse(result, "Delete must fail for non-existent batch");
    }

    @Test
    void delete_waitVerification1Batch_returnsTrue() {
        GajiBatchRoot entity = new GajiBatchRoot();
        entity.setId("202609-001");
        entity.setStatus(EProsesGaji.WAIT_VERIFICATION_PHASE_1);
        when(repository.findById("202609-001")).thenReturn(Optional.of(entity));
        when(repository.save(any(GajiBatchRoot.class))).thenReturn(entity);

        boolean result = commandService.delete("202609-001");

        assertTrue(result, "Delete should succeed for WAIT_VERIFICATION_PHASE_1 batch");
    }

    @Test
    void delete_setsIsDeletedViaRepository() {
        GajiBatchRoot entity = new GajiBatchRoot();
        entity.setId("202609-001");
        entity.setStatus(EProsesGaji.PENDING);
        entity.setIsDeleted(false);
        when(repository.findById("202609-001")).thenReturn(Optional.of(entity));
        when(repository.save(any(GajiBatchRoot.class))).thenReturn(entity);

        commandService.delete("202609-001");

        verify(repository).save(argThat(e -> {
            GajiBatchRoot root = (GajiBatchRoot) e;
            return root.getIsDeleted();
        }));
    }
}
