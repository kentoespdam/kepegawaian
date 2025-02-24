package id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootProcessRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootResponse;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRootErrorLogs.GajiBatchRootErrorLogsResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GajiBatchRootService {
    Page<GajiBatchRootResponse> findAll(GajiBatchRootRequest request);

    List<GajiBatchRootErrorLogsResponse> findErrorLogs(String id);

    SavedStatus<?> save(GajiBatchRootPostRequest request);

    SavedStatus<?> reprocess(String id, GajiBatchRootProcessRequest request);

    SavedStatus<?> verify1(String id, @Valid GajiBatchRootProcessRequest request);

    SavedStatus<?> verify2(String id, @Valid GajiBatchRootProcessRequest request);

    SavedStatus<?> accept(String id, @Valid GajiBatchRootProcessRequest request);

    boolean delete(String id);
}
