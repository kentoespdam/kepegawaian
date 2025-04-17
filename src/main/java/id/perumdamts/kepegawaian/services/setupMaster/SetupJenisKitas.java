package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.perumdamts.kepegawaian.entities.master.JenisKitas;
import id.perumdamts.kepegawaian.repositories.master.JenisKitasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetupJenisKitas implements SetupMaster {
    @Autowired
    private JenisKitasRepository repository;

    @Override
    public void insertBatch() throws JsonProcessingException {
        List<JenisKitas> list = new ArrayList<>();
        list.add(new JenisKitas(1L, "KTP"));
        list.add(new JenisKitas(1L, "NPWP"));
        list.add(new JenisKitas(2L, "Jamsostek"));
        list.add(new JenisKitas(3L, "ASKES"));
        list.add(new JenisKitas(4L, "KTP"));
        list.add(new JenisKitas(5L, "SIM"));
        list.add(new JenisKitas(6L, "Dapenma"));
        list.add(new JenisKitas(7L, "JPn"));
        list.add(new JenisKitas(8L, "Yakan"));
        list.add(new JenisKitas(9L, "Inkop Pamsi"));
        list.add(new JenisKitas(10L, "Korpri"));
        list.add(new JenisKitas(11L, "ID Card"));
        repository.saveAll(list);
    }
}
