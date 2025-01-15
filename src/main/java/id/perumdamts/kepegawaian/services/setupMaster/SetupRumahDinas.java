package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.perumdamts.kepegawaian.entities.master.RumahDinas;
import id.perumdamts.kepegawaian.repositories.master.RumahDinasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetupRumahDinas implements SetupMaster {
    @Autowired
    private RumahDinasRepository repository;

    @Override
    public void insertBatch() throws JsonProcessingException {
        List<RumahDinas> list = new ArrayList<>();
        list.add(new RumahDinas(1L, "R Jabatan Tanjung 110", 77000D));
        list.add(new RumahDinas(2L, "R Jabatan Tanjung 80", 56000D));
        list.add(new RumahDinas(3L, "RD Tanjung", 42000D));
        list.add(new RumahDinas(4L, "RD Pabuaran", 31500D));
        list.add(new RumahDinas(5L, "RD Ajibarang", 33600D));
        list.add(new RumahDinas(6L, "RD Rumah", 35000D));
        list.add(new RumahDinas(7L, "RJ Jipang", 39200D));
        list.add(new RumahDinas(8L, "RJ Kawungcarang", 29400D));

        repository.saveAll(list);
    }
}
