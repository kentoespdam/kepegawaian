package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.repositories.master.JenjangPendidikanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SetupJenjangPendidikan implements SetupMaster {
    private final JenjangPendidikanRepository jenjangPendidikanRepository;

    @Override
    public void insertBatch() {
        List<JenjangPendidikan> list = new ArrayList<>();
        list.add(new JenjangPendidikan("SD - Sederajat", "SD", 1, Boolean.TRUE));
        list.add(new JenjangPendidikan("SMP - Sederajat", "SMP", 2, Boolean.TRUE));
        list.add(new JenjangPendidikan("SMA - Sederajat", "SMA", 3, Boolean.TRUE));
        list.add(new JenjangPendidikan("Diploma 1 ", "D1", 4, Boolean.TRUE));
        list.add(new JenjangPendidikan("Diploma 2 ", "D2", 5, Boolean.TRUE));
        list.add(new JenjangPendidikan("Diploma 3 ", "D3", 6, Boolean.TRUE));
        list.add(new JenjangPendidikan("S1", "S1", 7, Boolean.TRUE));
        list.add(new JenjangPendidikan("S2", "S2", 8, Boolean.TRUE));
        list.add(new JenjangPendidikan("S3", "S3", 9, Boolean.FALSE));

        jenjangPendidikanRepository.saveAll(list);
    }
}
