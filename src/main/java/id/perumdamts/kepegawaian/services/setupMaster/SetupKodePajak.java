package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import id.perumdamts.kepegawaian.entities.master.KodePajak;
import id.perumdamts.kepegawaian.repositories.master.KodePajakRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetupKodePajak implements SetupMaster {
    @Autowired
    private KodePajakRepository repository;

    @Override
    public void insertBatch() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonString = "[{\"kode\":\"K/0\",\"nominal\":4875000},{\"kode\":\"K/1\",\"nominal\":5250000}]";
        List<KodePajak> list = mapper.readValue(jsonString, new TypeReference<>() {
        });
        repository.saveAll(list);
    }
}
