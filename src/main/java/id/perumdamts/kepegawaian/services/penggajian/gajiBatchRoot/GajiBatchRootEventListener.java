package id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.services.penggajian.gajiBatchProses.GajiBatchProsesCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class GajiBatchRootEventListener {
    private final GajiBatchProsesCommandService prosesCommandService;

    @Async("gajiProsesExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBatchRootProcess(GajiBatchRootProcessEvent event) {
        log.info("Processing batch {} after commit", event.getRootBatchId());
        prosesCommandService.prosesGaji(event.getRootBatchId());
    }
}
