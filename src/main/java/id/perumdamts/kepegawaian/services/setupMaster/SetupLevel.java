package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.repositories.master.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SetupLevel implements SetupMaster {
    private final LevelRepository levelRepository;

    @Override
    public void insertBatch() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonLevel = "[{\"id\":1,\"nama\":\"DEWAS\"},{\"id\":2,\"nama\":\"DIRUT\"},{\"id\":3,\"nama\":\"DIRTEK\"},{\"id\":4,\"nama\":\"DIRUM\"},{\"id\":5,\"nama\":\"MANAJER\"},{\"id\":6,\"nama\":\"SUPERVISOR\"},{\"id\":7,\"nama\":\"STAFF\"}]";
        List<Level> levelList = mapper.readValue(jsonLevel, new TypeReference<>() {
        });
        levelRepository.saveAll(levelList);

    }
}
