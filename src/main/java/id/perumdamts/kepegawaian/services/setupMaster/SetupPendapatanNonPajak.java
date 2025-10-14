package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiPendapatanNonPajakRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SetupPendapatanNonPajak implements SetupMaster {
    private final GajiPendapatanNonPajakRepository repository;

    @Override
    public void insertBatch() {
        List<GajiPendapatanNonPajak> list = new ArrayList<>();
        list.add(new GajiPendapatanNonPajak("TK", 4500000D, ""));
        list.add(new GajiPendapatanNonPajak("K/0", 4875000D, ""));
        list.add(new GajiPendapatanNonPajak("K/1", 5250000D, ""));
        list.add(new GajiPendapatanNonPajak("K/2", 5625000D, ""));
        list.add(new GajiPendapatanNonPajak("K/3", 6000000D, ""));
        list.add(new GajiPendapatanNonPajak("TK/3", 5625000D, ""));
        list.add(new GajiPendapatanNonPajak("TK/1", 4875000D, ""));
        list.add(new GajiPendapatanNonPajak("TK/2", 5250000D, ""));

        repository.saveAll(list);
    }
}
