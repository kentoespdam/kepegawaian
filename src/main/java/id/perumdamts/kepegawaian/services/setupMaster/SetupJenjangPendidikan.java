package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.repositories.master.JenjangPendidikanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetupJenjangPendidikan implements SetupMaster {
    @Autowired
    private JenjangPendidikanRepository jenjangPendidikanRepository;

    @Override
    public void insertBatch() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonData = "[{\"nama\":\"SD - Sederajat\",\"seq\":1},{\"nama\":\"SMP - Sederajat\",\"seq\":2},{\"nama\":\"SMA - Sederajat\",\"seq\":3},{\"nama\":\"Diploma 1\",\"seq\":4},{\"nama\":\"Diploma 2\",\"seq\":5},{\"nama\":\"Diploma 3\",\"seq\":6},{\"nama\":\"S1\",\"seq\":7},{\"nama\":\"S2\",\"seq\":8},{\"nama\":\"S3\",\"seq\":9}]";
        List<JenjangPendidikan> jenjangPendidikanList = mapper.readValue(jsonData, new TypeReference<>() {
        });
        jenjangPendidikanRepository.saveAll(jenjangPendidikanList);
    }
}
