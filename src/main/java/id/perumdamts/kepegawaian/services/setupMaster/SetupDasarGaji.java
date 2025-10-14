package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.penggajian.DasarGaji;
import id.perumdamts.kepegawaian.repositories.penggajian.DasarGajiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SetupDasarGaji implements SetupMaster {
    private final DasarGajiRepository repository;

    @Override
    public void insertBatch() {
        DasarGaji dasarGaji = new DasarGaji();
        dasarGaji.setDeskripsi("Skala Gaji - PP No.30 Thn 2015");
        dasarGaji.setTanggalAwal(LocalDate.of(2015, 1, 1));
        dasarGaji.setTanggalAkhir(LocalDate.of(2025, 12, 31));
        dasarGaji.setAktif(true);
        repository.save(dasarGaji);
    }
}
