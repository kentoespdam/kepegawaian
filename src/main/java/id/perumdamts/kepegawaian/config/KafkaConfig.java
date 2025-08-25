package id.perumdamts.kepegawaian.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConfig {
    @Value("${spring.kafka.topic}")
    private String PENGGAJIAN_TOPIC;

    @Bean
    public NewTopic penggajianTopic() {
        log.info("kafka topic: {}", PENGGAJIAN_TOPIC);
        return TopicBuilder.name(PENGGAJIAN_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }


//    @KafkaListener(id = PENGGAJIAN_ID, topics = PENGGAJIAN_TOPIC)
//    public void penggajianListener(String message) {
//        System.out.println("Message received: " + message);
//    }
}
