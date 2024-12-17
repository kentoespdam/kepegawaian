package id.perumdamts.kepegawaian.services.master.alasanBerhenti;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiPostRequest;
import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiPutRequest;
import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiRequest;
import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AlasanBerhentiService {
    List<AlasanBerhentiResponse> findAll();

    Page<AlasanBerhentiResponse> findPage(AlasanBerhentiRequest request);

    AlasanBerhentiResponse findById(Long id);

    SavedStatus<?> save(AlasanBerhentiPostRequest request);

    SavedStatus<?> saveBatch(List<AlasanBerhentiPostRequest> requests);

    SavedStatus<?> update(Long id, AlasanBerhentiPutRequest request);

    boolean deleteById(Long id);
}
