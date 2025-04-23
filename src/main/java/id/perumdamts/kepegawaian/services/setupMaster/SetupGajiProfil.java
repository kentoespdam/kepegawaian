package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiProfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetupGajiProfil implements  SetupMaster {
    @Autowired
    private GajiProfilRepository repository;
    @Override
    public void insertBatch() throws JsonProcessingException {
        List<GajiProfil> list=new ArrayList<>();
        list.add(new GajiProfil(1L, "Profil Komponen dan Formula Gaji Direktur"));
        list.add(new GajiProfil(2L, "Profil Komponen dan Formula Gaji Pegawai Tetap"));
        list.add(new GajiProfil(3L, "Profil Komponen dan Formula Gaji Calon Pegawai Tetap"));
        list.add(new GajiProfil(4L, "Profil Komponen dan Formula Gaji Calon Pegawai Honorer Tetap"));
        list.add(new GajiProfil(5L, "Profil Komponen dan Formula Gaji Pegawai Honorer Tetap"));
        list.add(new GajiProfil(6L, "Profil Komponen dan Formula Gaji Pegawai Kontrak"));
        list.add(new GajiProfil(7L, "Profil Komponen dan Formula Gaji Suami Istri se Kantor"));
        list.add(new GajiProfil(8L, "Profil Komponen & Formula Gaji Capeg < UMK"));
        list.add(new GajiProfil(9L, "Profil Komponen dan Formula Gaji Direktur Utama"));

        repository.saveAll(list);
    }
}
