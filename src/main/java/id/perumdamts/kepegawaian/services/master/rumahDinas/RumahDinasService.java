package id.perumdamts.kepegawaian.services.master.rumahDinas;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasPostRequest;
import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasPutRequest;
import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasRequest;
import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RumahDinasService {
    Page<RumahDinasResponse> findPage(RumahDinasRequest request);
    List<RumahDinasResponse> findAll(RumahDinasRequest request);
    RumahDinasResponse findById(Long id);
    SavedStatus<?> save(RumahDinasPostRequest request);
    SavedStatus<?> update(Long id, RumahDinasPutRequest request);
    Boolean delete(Long id);
}
