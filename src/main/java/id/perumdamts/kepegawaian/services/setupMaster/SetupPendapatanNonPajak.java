package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiPendapatanNonPajakRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetupPendapatanNonPajak implements SetupMaster {
    @Autowired
    private GajiPendapatanNonPajakRepository repository;

    @Override
    public void insertBatch() throws JsonProcessingException {
        List<GajiPendapatanNonPajak> list = new ArrayList<>();
        list.add(new GajiPendapatanNonPajak(1L, "TK", 4500000D, ""));
        list.add(new GajiPendapatanNonPajak(2L, "K/0", 4875000D, ""));
        list.add(new GajiPendapatanNonPajak(3L, "K/1", 5250000D, ""));
        list.add(new GajiPendapatanNonPajak(4L, "K/2", 5625000D, ""));
        list.add(new GajiPendapatanNonPajak(5L, "K/3", 6000000D, ""));
        list.add(new GajiPendapatanNonPajak(6L, "TK/3", 5625000D, ""));
        list.add(new GajiPendapatanNonPajak(7L, "TK/1", 4875000D, ""));
        list.add(new GajiPendapatanNonPajak(8L, "TK/2", 5250000D, ""));

        repository.saveAll(list);
    }
}
