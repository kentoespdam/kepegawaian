package id.perumdamts.kepegawaian.services.master.pangkat;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.pangkat.PangkatPostRequest;
import id.perumdamts.kepegawaian.dto.master.pangkat.PangkatRequest;
import id.perumdamts.kepegawaian.dto.master.pangkat.PangkatResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PangkatService {
    List<PangkatResponse> findAll();
    Page<PangkatResponse> findPage(PangkatRequest request);
    PangkatResponse findById(Long id);
    SavedStatus<?> save(PangkatPostRequest request);
    SavedStatus<?> saveBatch(List<PangkatPostRequest> requests);
    SavedStatus<?> update(Long id, PangkatPostRequest request);
    Boolean deleteById(Long id);
}
