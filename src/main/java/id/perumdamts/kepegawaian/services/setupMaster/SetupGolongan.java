package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.repositories.master.GolonganRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SetupGolongan implements SetupMaster {
    private final GolonganRepository repository;

    @Override
    public void insertBatch() throws JsonProcessingException {
        List<Golongan> list = new ArrayList<>();
        list.add(new Golongan(1L, "A.1", "Pegawai Dasar Muda"));
        list.add(new Golongan(2L, "A.2", "Pegawai Dasar Muda Tk.I"));
        list.add(new Golongan(3L, "A.3", "Pegawai Dasar"));
        list.add(new Golongan(4L, "A.4", "Pegawai Dasar Tk.I"));
        list.add(new Golongan(5L, "B.1", "Pelaksana Muda"));
        list.add(new Golongan(6L, "B.2", "Pelaksana Muda Tk.I"));
        list.add(new Golongan(7L, "B.3", "Pelaksana"));
        list.add(new Golongan(8L, "B.4", "Pelaksana Tk.I"));
        list.add(new Golongan(9L, "C.1", "Staf Muda"));
        list.add(new Golongan(10L, "C.2", "Staf Muda Tk.I"));
        list.add(new Golongan(11L, "C.3", "Staf"));
        list.add(new Golongan(12L, "C.4", "Staf Tk.I"));
        list.add(new Golongan(13L, "D.1", "Manajer Muda"));
        list.add(new Golongan(14L, "D.2", "Manajer Muda Tk.I"));
        list.add(new Golongan(15L, "D.3", "Manajer"));
        list.add(new Golongan(16L, "D.4", "Manajer Tk.I"));
        list.add(new Golongan(17L, "D.5", "Manajer Utama"));
        list.add(new Golongan(18L, "A.3 (C)", "Pegawai Dasar (Capeg)"));

        repository.saveAll(list);
    }
}
