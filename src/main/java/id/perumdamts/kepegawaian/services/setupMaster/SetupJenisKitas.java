package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.master.JenisKitas;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenisKitasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SetupJenisKitas implements SetupMaster {
    private final JenisKitasRepository repository;

    @Override
    public void insertBatch() {
        List<JenisKitas> list = new ArrayList<>();
        list.add(new JenisKitas("KTP"));
        list.add(new JenisKitas("NPWP"));
        list.add(new JenisKitas("Jamsostek"));
        list.add(new JenisKitas("ASKES"));
        list.add(new JenisKitas("KTP"));
        list.add(new JenisKitas("SIM"));
        list.add(new JenisKitas("Dapenma"));
        list.add(new JenisKitas("JPn"));
        list.add(new JenisKitas("Yakan"));
        list.add(new JenisKitas("Inkop Pamsi"));
        list.add(new JenisKitas("Korpri"));
        list.add(new JenisKitas("ID Card"));
        repository.saveAll(list);
    }
}
