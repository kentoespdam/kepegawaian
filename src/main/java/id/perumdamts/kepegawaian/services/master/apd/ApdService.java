package id.perumdamts.kepegawaian.services.master.apd;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.apd.ApdPostRequest;
import id.perumdamts.kepegawaian.dto.master.apd.ApdPutRequest;
import id.perumdamts.kepegawaian.dto.master.apd.ApdRequest;
import id.perumdamts.kepegawaian.dto.master.apd.ApdResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ApdService {
    List<ApdResponse> findAll();

    Page<ApdResponse> findPage(ApdRequest request);

    ApdResponse findById(Long id);

    List<ApdResponse> findByProfesiId(Long id);

    SavedStatus<?> save(ApdPostRequest request);

    SavedStatus<?> saveBatch(List<ApdPostRequest> requests);

    SavedStatus<?> update(Long id, ApdPutRequest request);

    boolean deleteById(Long id);
}
