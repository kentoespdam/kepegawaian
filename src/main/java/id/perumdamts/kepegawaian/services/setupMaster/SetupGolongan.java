package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.repositories.master.GolonganRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SetupGolongan implements SetupMaster {
    private final GolonganRepository repository;

    @Override
    public void insertBatch() {
        List<Golongan> list = List.of(
                new Golongan("A.1", "Pegawai Dasar Muda"),
                new Golongan("A.2", "Pegawai Dasar Muda Tk.I"),
                new Golongan("A.3", "Pegawai Dasar"),
                new Golongan("A.4", "Pegawai Dasar Tk.I"),
                new Golongan("B.1", "Pelaksana Muda"),
                new Golongan("B.2", "Pelaksana Muda Tk.I"),
                new Golongan("B.3", "Pelaksana"),
                new Golongan("B.4", "Pelaksana Tk.I"),
                new Golongan("C.1", "Staf Muda"),
                new Golongan("C.2", "Staf Muda Tk.I"),
                new Golongan("C.3", "Staf"),
                new Golongan("C.4", "Staf Tk.I"),
                new Golongan("D.1", "Manajer Muda"),
                new Golongan("D.2", "Manajer Muda Tk.I"),
                new Golongan("D.3", "Manajer"),
                new Golongan("D.4", "Manajer Tk.I"),
                new Golongan("D.5", "Manajer Utama"),
                new Golongan("A.3 (C)", "Pegawai Dasar (Capeg)")
        );
        repository.saveAllAndFlush(list);
    }
}
