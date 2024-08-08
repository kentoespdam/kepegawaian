package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import id.perumdamts.kepegawaian.entities.master.StatusPegawai;
import id.perumdamts.kepegawaian.repositories.master.StatusPegawaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SetupStatusPegawai implements SetupMaster{
    private final StatusPegawaiRepository repository;

    @Override
    public void insertBatch() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonStatusPegawai = "[{\"id\":1,\"nama\":\"KONTRAK\"},{\"id\":2,\"nama\":\"CAPEG\"},{\"id\":3,\"nama\":\"PEGAWAI\"},{\"id\":4,\"nama\":\"HONORER TETAP\"}]";
        List<StatusPegawai> statusPegawaiList = mapper.readValue(jsonStatusPegawai, new TypeReference<>() {
        });
        repository.saveAll(statusPegawaiList);
    }
}
