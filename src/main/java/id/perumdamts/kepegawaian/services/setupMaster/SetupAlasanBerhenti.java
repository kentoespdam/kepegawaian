package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import id.perumdamts.kepegawaian.entities.master.AlasanBerhenti;
import id.perumdamts.kepegawaian.repositories.master.AlasanBerhentiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetupAlasanBerhenti implements SetupMaster {
    @Autowired
    private AlasanBerhentiRepository repository;

    @Override
    public void insertBatch() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonString = "[{\"nama\":\"Mengundurkan Diri\"},{\"nama\":\"Diberhentikan\"},{\"nama\":\"Kontrak Berakhir\"},{\"nama\":\"Pensiun Normal\"},{\"nama\":\"Meninggal Dunia\"}]";
        List<AlasanBerhenti> list = mapper.readValue(jsonString, new TypeReference<>() {
        });
        repository.saveAll(list);
    }
}
