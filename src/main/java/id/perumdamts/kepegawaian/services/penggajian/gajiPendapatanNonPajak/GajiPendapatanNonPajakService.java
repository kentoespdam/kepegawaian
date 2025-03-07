package id.perumdamts.kepegawaian.services.penggajian.gajiPendapatanNonPajak;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GajiPendapatanNonPajakService {
    Page<GajiPendapatanNonPajakResponse> findPage(GajiPendapatanNonPajakRequest request);
    List<GajiPendapatanNonPajakResponse> findAll();
    GajiPendapatanNonPajakResponse findById(Long id);
    SavedStatus<?> save(GajiPendapatanNonPajakPostRequest request);
    SavedStatus<?> update(Long id, GajiPendapatanNonPajakPutRequest request);
    Boolean delete(Long id);
}
