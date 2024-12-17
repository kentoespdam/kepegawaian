package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import id.perumdamts.kepegawaian.entities.master.JenisPelatihan;
import id.perumdamts.kepegawaian.repositories.master.JenisPelatihanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetupJenisPelatihan implements SetupMaster {
    @Autowired
    private JenisPelatihanRepository jenisPelatihanRepository;

    @Override
    public void insertBatch() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonData = "[{\"nama\":\"Administrasi\"},{\"nama\":\"Keuangan \"},{\"nama\":\"Pelayanan \"},{\"nama\":\"IT\"},{\"nama\":\"Perpipaan\"},{\"nama\":\"Listrik & Perpompaan\"},{\"nama\":\"Pengolahan\"},{\"nama\":\"SPAM\"}]";
        List<JenisPelatihan> jenisPelatihanList = mapper.readValue(jsonData, new TypeReference<>() {
        });
        jenisPelatihanRepository.saveAll(jenisPelatihanList);
    }
}
