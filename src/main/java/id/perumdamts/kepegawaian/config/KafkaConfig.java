package id.perumdamts.kepegawaian.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

@Component
public class KafkaConfig {
    public final static String PENGGAJIAN_TOPIC = "penggajian";
    public final static String HITUNG_ULANG_TOPIC = "hitung_ulang";

    @Bean
    public NewTopic penggajianTopic() {
        return TopicBuilder.name(PENGGAJIAN_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic hitungUlangTopic() {
        return TopicBuilder.name(HITUNG_ULANG_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

//    @KafkaListener(id = PENGGAJIAN_ID, topics = PENGGAJIAN_TOPIC)
//    public void penggajianListener(String message) {
//        System.out.println("Message received: " + message);
//    }
}
