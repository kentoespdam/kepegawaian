package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import id.perumdamts.kepegawaian.entities.master.JenisMutasi;
import id.perumdamts.kepegawaian.repositories.master.JenisMutasiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetupJenisMutasi implements SetupMaster {
    @Autowired
    private JenisMutasiRepository repository;

    @Override
    public void insertBatch() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonString = "[{\"nama\":\"Pengangkatan Pertama\"},{\"nama\":\"Pindah Loker / Jabatan\"},{\"nama\":\"Perubahan Gaji\"},{\"nama\":\"Terminasi\"}]";
        List<JenisMutasi> list = mapper.readValue(jsonString, new TypeReference<>() {
        });
        repository.saveAll(list);
    }
}
