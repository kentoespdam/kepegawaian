package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.master.RumahDinas;
import id.perumdamts.kepegawaian.repositories.master.jpa.RumahDinasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SetupRumahDinas implements SetupMaster {
    private final RumahDinasRepository repository;

    @Override
    public void insertBatch() {
        List<RumahDinas> list = new ArrayList<>();
        list.add(new RumahDinas("R Jabatan Tanjung 110", 77000D));
        list.add(new RumahDinas("R Jabatan Tanjung 80", 56000D));
        list.add(new RumahDinas("RD Tanjung", 42000D));
        list.add(new RumahDinas("RD Pabuaran", 31500D));
        list.add(new RumahDinas("RD Ajibarang", 33600D));
        list.add(new RumahDinas("RD Rumah", 35000D));
        list.add(new RumahDinas("RJ Jipang", 39200D));
        list.add(new RumahDinas("RJ Kawungcarang", 29400D));

        repository.saveAll(list);
    }
}
