package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import id.perumdamts.kepegawaian.entities.penggajian.DasarGaji;
import id.perumdamts.kepegawaian.repositories.penggajian.DasarGajiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SetupDasarGaji implements SetupMaster {
    @Autowired
    private DasarGajiRepository repository;

    @Override
    public void insertBatch() throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DasarGaji dasarGaji = new DasarGaji();
        dasarGaji.setDeskripsi("Skala Gaji - PP No.30 Thn 2015");
        dasarGaji.setTanggalAwal(LocalDate.of(2015, 1, 1));
        dasarGaji.setTanggalAkhir(LocalDate.of(2025, 12, 31));
        dasarGaji.setAktif(true);
        repository.save(dasarGaji);
    }
}
