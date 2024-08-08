package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.repositories.master.GolonganRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SetupGolongan implements SetupMaster {
    private final GolonganRepository golonganRepository;
    @Override
    public void insertBatch() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonGolongan = "[{\"golongan\":\"A.1\",\"pangkat\":\"Pegawai Dasar Muda\"},{\"golongan\":\"A.2\",\"pangkat\":\"Pegawai Dasar Muda Tk.I\"},{\"golongan\":\"A.3\",\"pangkat\":\"Pegawai Dasar\"},{\"golongan\":\"A.4\",\"pangkat\":\"Pegawai Dasar Tk.I\"},{\"golongan\":\"B.1\",\"pangkat\":\"Pelaksana Muda\"},{\"golongan\":\"B.2\",\"pangkat\":\"Pelaksana Muda Tk.I\"},{\"golongan\":\"B.3\",\"pangkat\":\"Pelaksana\"},{\"golongan\":\"B.4\",\"pangkat\":\"Pelaksana Tk.I\"},{\"golongan\":\"C.1\",\"pangkat\":\"Staf Muda\"},{\"golongan\":\"C.2\",\"pangkat\":\"Staf Muda Tk.I\"},{\"golongan\":\"C.3\",\"pangkat\":\"Staf\"},{\"golongan\":\"C.4\",\"pangkat\":\"Staf Tk.I\"},{\"golongan\":\"D.1\",\"pangkat\":\"Manajer Muda\"},{\"golongan\":\"D.2\",\"pangkat\":\"Manajer Muda Tk.I\"},{\"golongan\":\"D.3\",\"pangkat\":\"Manajer\"},{\"golongan\":\"D.4\",\"pangkat\":\"Manajer Tk.I\"},{\"golongan\":\"D.5\",\"pangkat\":\"Manajer Utama\"}]";
        List<Golongan> golonganList = mapper.readValue(jsonGolongan, new TypeReference<>() {
        });
        golonganRepository.saveAll(golonganList);
    }
}
