package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import id.perumdamts.kepegawaian.entities.master.JenisKeahlian;
import id.perumdamts.kepegawaian.repositories.master.JenisKeahlianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetupJenisKeahlian implements SetupMaster {
    @Autowired
    private JenisKeahlianRepository jenisKeahlianRepository;

    @Override
    public void insertBatch() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonData = "[{\"nama\":\"Pemrograman\"},{\"nama\":\"Desain Grafis\"},{\"nama\":\"Desain Grafis\"},{\"nama\":\"Bhs. Inggris\"},{\"nama\":\"Teknisi Komputer\"},{\"nama\":\"Ahli MAM Muda\"},{\"nama\":\"Ahli MAM Madya\"},{\"nama\":\"Ahli MAM Utama\"},{\"nama\":\"Ahli Akuntansi\"},{\"nama\":\"Ahli Pengadaan\"},{\"nama\":\"Assessor\"}]";
        List<JenisKeahlian> jenisKeahlianList = mapper.readValue(jsonData, new TypeReference<>() {
        });
        jenisKeahlianRepository.saveAll(jenisKeahlianList);
    }
}
