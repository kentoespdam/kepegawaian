package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMaster;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface GajiBatchMasterService {
    List<GajiBatchMasterResponse> findAll(GajiBatchMasterRequest request);

    GajiBatchMasterResponse findById(Long id);

    ResponseEntity<?> downloadTableGaji(String rootBatchId);

    ResponseEntity<?> downloadPotonganGaji(String rootBatchId);

    SavedStatus<?> uploadPotonganTambahan(String rootBatchId, GajiBatchMasterPostRequest request);
}
