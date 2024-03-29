package id.perumdamts.kepegawaian.services.master.profesi;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiPostRequest;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiRequest;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProfesiService {
    List<ProfesiResponse> findAll();

    Page<ProfesiResponse> findPage(ProfesiRequest request);

    ProfesiResponse findById(Long id);

    SavedStatus<?> save(ProfesiPostRequest request);

    SavedStatus<?> saveBatch(List<ProfesiPostRequest> requests);

    SavedStatus<?> update(Long id, ProfesiPostRequest request);

    Boolean deleteById(Long id);
}
