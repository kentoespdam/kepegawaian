package id.perumdamts.kepegawaian.controllers.master;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.perumdamts.kepegawaian.entities.master.Grade;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
class SetupMasterControllerTest {
    ObjectMapper mapper = new ObjectMapper();
    @Test
    void jajal(){
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonString = "[{\"grade\":1,\"tukin\":3000000,\"levelId\":5},{\"grade\":2,\"tukin\":3150000,\"levelId\":5},{\"grade\":3,\"tukin\":3300000,\"levelId\":5},{\"grade\":1,\"tukin\":1930500,\"levelId\":6},{\"grade\":2,\"tukin\":2216500,\"levelId\":6},{\"grade\":4,\"tukin\":3500000,\"levelId\":5},{\"grade\":3,\"tukin\":2502500,\"levelId\":6},{\"grade\":1,\"tukin\":715000,\"levelId\":7},{\"grade\":2,\"tukin\":1072500,\"levelId\":7},{\"grade\":3,\"tukin\":1430000,\"levelId\":7},{\"grade\":4,\"tukin\":1787500,\"levelId\":7}]";
        try {
            List<Grade> list = mapper.readValue(jsonString, new TypeReference<>() {
            });
            log.info("list: {}", list);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}