package id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GajiBatchRootEventPublisher {
    private final ApplicationEventPublisher publisher;

    public void publishAfterCommit(String batchId) {
        // AFTER_COMMIT dijamin listener (@TransactionalEventListener phase AFTER_COMMIT),
        // jadi publish cukup di dalam transaksi pemanggil — ganti pola manual
        // TransactionSynchronization + Kafka (ADR-0024 digantikan keputusan #1 claim order).
        publisher.publishEvent(new GajiBatchRootProcessEvent(this, batchId));
    }
}
