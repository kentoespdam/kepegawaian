package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import id.perumdamts.kepegawaian.entities.master.StatusKerja;
import id.perumdamts.kepegawaian.repositories.master.StatusKerjaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetupStatusKerja implements SetupMaster {
    @Autowired
    private StatusKerjaRepository statusKerjaRepository;

    @Override
    public void insertBatch() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonStatusKerja = "[{\"nama\":\"Karyawan Aktif\"},{\"nama\":\"Dirumahkan\"},{\"nama\":\"Berhenti/Keluar\"}]";
        List<StatusKerja> list = mapper.readValue(jsonStatusKerja, new TypeReference<>() {
        });
        statusKerjaRepository.saveAll(list);
    }
}
