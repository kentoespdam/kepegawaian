package id.perumdamts.kepegawaian.services.master.kodePajak;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.kodePajak.KodePajakPostRequest;
import id.perumdamts.kepegawaian.dto.master.kodePajak.KodePajakPutRequest;
import id.perumdamts.kepegawaian.dto.master.kodePajak.KodePajakRequest;
import id.perumdamts.kepegawaian.dto.master.kodePajak.KodePajakResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface KodePajakService {
    List<KodePajakResponse> findAll();

    Page<KodePajakResponse> findPage(KodePajakRequest request);

    KodePajakResponse findById(Long id);

    SavedStatus<?> save(KodePajakPostRequest request);

    SavedStatus<?> saveBatch(List<KodePajakPostRequest> requests);

    SavedStatus<?> update(Long id, KodePajakPutRequest request);

    boolean deleteById(Long id);
}
