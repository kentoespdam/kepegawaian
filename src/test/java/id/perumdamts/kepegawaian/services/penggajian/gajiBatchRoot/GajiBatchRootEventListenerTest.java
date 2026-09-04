package id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.services.penggajian.gajiBatchProses.GajiBatchProsesCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

/**
 * Unit test {@link GajiBatchRootEventListener} — event after-commit harus
 * didelegasikan ke {@link GajiBatchProsesCommandService#prosesGaji(String)}.
 */
@ExtendWith(MockitoExtension.class)
class GajiBatchRootEventListenerTest {
    private static final String BATCH_ID = "202609-001";

    @Mock
    private GajiBatchProsesCommandService prosesCommandService;

    private GajiBatchRootEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new GajiBatchRootEventListener(prosesCommandService);
    }

    @Test
    void onBatchRootProcess_delegatesToProsesCommandService() {
        GajiBatchRootProcessEvent event = new GajiBatchRootProcessEvent(this, BATCH_ID);

        listener.onBatchRootProcess(event);

        verify(prosesCommandService).prosesGaji(BATCH_ID);
    }
}
