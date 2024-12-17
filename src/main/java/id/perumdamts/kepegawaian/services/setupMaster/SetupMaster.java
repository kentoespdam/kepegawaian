package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface SetupMaster {
    ObjectMapper mapper = new ObjectMapper();

    void insertBatch() throws JsonProcessingException;
}
