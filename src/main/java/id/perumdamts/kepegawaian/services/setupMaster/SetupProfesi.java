package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.repositories.master.ProfesiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetupProfesi implements SetupMaster {
    @Autowired
    private ProfesiRepository profesiRepository;

    @Override
    public void insertBatch() {
        List<Profesi> list = new ArrayList<>();
        list.add(new Profesi(1L, new Level(5L), "Manajer Perencanaan & Pengembangan", "-", "-"));
        list.add(new Profesi(2L, new Level(5L), "Manajer Produksi & Distribusi 1", "-", "-"));
        list.add(new Profesi(3L, new Level(5L), "Manajer Pengendalian Teknik", "-", "-"));
        list.add(new Profesi(4L, new Level(5L), "Manajer Keuangan", "-", "-"));
        list.add(new Profesi(5L, new Level(5L), "Manajer Bagian Sumber Daya Manusia & TI", "-", "-"));
        list.add(new Profesi(6L, new Level(6L), "Supervisor Bagian Sumber Daya Manusia & TI", "-", "-"));
        list.add(new Profesi(7L, new Level(7L), "Staf Sub Bag Teknologi Informasi (Programmer)", "-", "-"));
        profesiRepository.saveAll(list);
    }
}
