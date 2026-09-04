package id.perumdamts.kepegawaian.services.penggajian.gajiBatchProses;

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
import static org.mockito.Mockito.*;

/**
 * Unit test stub {@link GajiBatchProsesCommandService#prosesGaji(String)} —
 * Wave 2: batch PENDING dipindah ke PROSES. Snapshot/kalkulasi diisi Wave 7.
 */
@ExtendWith(MockitoExtension.class)
class GajiBatchProsesCommandServiceTest {
    private static final String BATCH_ID = "202609-001";

    @Mock
    private GajiBatchRootRepository repository;

    private GajiBatchProsesCommandService service;

    @BeforeEach
    void setUp() {
        service = new GajiBatchProsesCommandService(repository);
    }

    private GajiBatchRoot batch(EProsesGaji status) {
        GajiBatchRoot entity = new GajiBatchRoot();
        entity.setId(BATCH_ID);
        entity.setPeriode("202609");
        entity.setStatus(status);
        return entity;
    }

    @Test
    void prosesGaji_marksBatchAsProses() {
        GajiBatchRoot entity = batch(EProsesGaji.PENDING);
        when(repository.findById(BATCH_ID)).thenReturn(Optional.of(entity));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.prosesGaji(BATCH_ID);

        verify(repository).save(argThat(e -> {
            GajiBatchRoot root = (GajiBatchRoot) e;
            return root.getStatus() == EProsesGaji.PROSES && root.getTanggalProses() != null;
        }));
    }

    @Test
    void prosesGaji_throwsNotFound_whenBatchMissing() {
        when(repository.findById(BATCH_ID)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.prosesGaji(BATCH_ID));

        assertTrue(ex.getMessage().contains("Unknown Batch Process"),
                "Message must mention 'Unknown Batch Process', got: " + ex.getMessage());
        verify(repository, never()).save(any());
    }
}
