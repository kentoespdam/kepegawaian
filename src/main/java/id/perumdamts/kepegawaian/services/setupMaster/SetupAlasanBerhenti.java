package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.master.AlasanBerhenti;
import id.perumdamts.kepegawaian.repositories.master.jpa.AlasanBerhentiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SetupAlasanBerhenti implements SetupMaster {
    private final AlasanBerhentiRepository repository;

    @Override
    public void insertBatch() {
        List<AlasanBerhenti> list = new ArrayList<>();
        list.add(new AlasanBerhenti("Mengundurkan Diri", ""));
        list.add(new AlasanBerhenti("Diberhentikan", ""));
        list.add(new AlasanBerhenti("Kontrak Berakhir", ""));
        list.add(new AlasanBerhenti("Pensiun Normal", ""));
        list.add(new AlasanBerhenti("Meninggal Dunia", ""));
        repository.saveAll(list);
    }
}
