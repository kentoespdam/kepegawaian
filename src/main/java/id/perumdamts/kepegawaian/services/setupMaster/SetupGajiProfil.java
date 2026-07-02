package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiProfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SetupGajiProfil implements SetupMaster {
    private final GajiProfilRepository repository;

    @Override
    public void insertBatch() {
        List<GajiProfil> list = new ArrayList<>();
        list.add(new GajiProfil("Profil Komponen dan Formula Gaji Direktur"));
        list.add(new GajiProfil("Profil Komponen dan Formula Gaji Pegawai Tetap"));
        list.add(new GajiProfil("Profil Komponen dan Formula Gaji Calon Pegawai Tetap"));
        list.add(new GajiProfil("Profil Komponen dan Formula Gaji Calon Pegawai Honorer Tetap"));
        list.add(new GajiProfil("Profil Komponen dan Formula Gaji Pegawai Honorer Tetap"));
        list.add(new GajiProfil("Profil Komponen dan Formula Gaji Pegawai Kontrak"));
        list.add(new GajiProfil("Profil Komponen dan Formula Gaji Suami Istri se Kantor"));
        list.add(new GajiProfil("Profil Komponen & Formula Gaji Capeg < UMK"));
        list.add(new GajiProfil("Profil Komponen dan Formula Gaji Direktur Utama"));

        repository.saveAll(list);
    }
}
