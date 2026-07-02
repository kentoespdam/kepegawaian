package id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@Slf4j
@RequiredArgsConstructor
public class GajiBatchRootEventPublisher {
    @Value("${spring.kafka.topic}")
    private String topic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishAfterCommit(String batchId) {
        if (kafkaTemplate == null) {
            log.warn("kafkaTemplate is null; skipping publish for batch {}", batchId);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                kafkaTemplate.send(topic, batchId).whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish batch {} to topic {}", batchId, topic, ex);
                    } else if (result != null && result.getRecordMetadata() != null) {
                        log.info("Published batch {} to topic {} partition={} offset={}",
                                batchId, topic,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
            }
        });
    }
}
