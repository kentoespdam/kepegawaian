package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GajiBatchMasterProsesService {
    Page<GajiBatchMasterProsesResponse> findPage(GajiBatchMasterProsesRequest request);

    GajiBatchMasterProsesResponse findById(Long id);

    List<GajiBatchMasterProsesResponse> findByMasterId(Long id);

    SavedStatus<?> save(GajiBatchMasterProsesPostRequest request);

    boolean rollback(Long rootBatchId);

    boolean delete(Long id);
}
