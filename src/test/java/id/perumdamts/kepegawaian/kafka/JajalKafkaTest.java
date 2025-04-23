package id.perumdamts.kepegawaian.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest
public class JajalKafkaTest {
    @Autowired
    private KafkaTemplate<String, String> template;

//    @Test
    public void test() {
        template.send("kepegawaian", "test");
    }
}
