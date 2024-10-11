package id.perumdamts.kepegawaian.services.kepegawaian.riwayatSp;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface RiwayatSpService {
    Page<RiwayatSpResponse> findPage(Long id,RiwayatSpRequest request);
    RiwayatSpResponse findById(Long id);
    ResponseEntity<?> getFile(Long id);
    SavedStatus<?> save(RiwayatSpPostRequest request);
    SavedStatus<?> update(Long id, RiwayatSpPutRequest request);

    boolean delete(Long id);
}
