package id.perumdamts.kepegawaian.services.penggajian.gajiPhdp;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GajiPhdpService {
    Page<GajiPhdpResponse> findPage(GajiPhdpRequest request);
    List<GajiPhdpResponse> findAll();
    GajiPhdpResponse findById(Long id);
    SavedStatus<?> save(GajiPhdpPostRequest request);
    SavedStatus<?> update(Long id, GajiPhdpPutRequest request);
    Boolean delete(Long id);
}
