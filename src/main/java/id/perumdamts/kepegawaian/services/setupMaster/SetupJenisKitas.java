package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import id.perumdamts.kepegawaian.entities.master.JenisKitas;
import id.perumdamts.kepegawaian.repositories.master.JenisKitasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetupJenisKitas implements SetupMaster {
    @Autowired
    private JenisKitasRepository jenisKitasRepository;

    @Override
    public void insertBatch() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonData = "[{\"nama\":\"KTP\"},{\"nama\":\"BPJS\"},{\"nama\":\"KK\"},{\"nama\":\"NPWP\"},{\"nama\":\"SIM\"},{\"nama\":\"Dapenma\"},{\"nama\":\"JPn\"},{\"nama\":\"Yakan\"},{\"nama\":\"Korpri\"},{\"nama\":\"ID Card\"}]";
        List<JenisKitas> jenisKitasList = mapper.readValue(jsonData, new TypeReference<>() {
        });
        jenisKitasRepository.saveAll(jenisKitasList);
    }
}
