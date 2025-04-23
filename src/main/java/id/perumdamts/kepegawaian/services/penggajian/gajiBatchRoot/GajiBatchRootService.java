package id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootProcessRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootResponse;
import org.springframework.data.domain.Page;

public interface GajiBatchRootService {
    Page<GajiBatchRootResponse> findAll(GajiBatchRootRequest request);

    SavedStatus<?> save(GajiBatchRootPostRequest request);

    SavedStatus<?> reprocess(String id, GajiBatchRootProcessRequest request);

    SavedStatus<?> verify1(String id, GajiBatchRootProcessRequest request);

    SavedStatus<?> verify2(String id, GajiBatchRootProcessRequest request);

    SavedStatus<?> accept(String id, GajiBatchRootProcessRequest request);

    boolean delete(String id);
}
