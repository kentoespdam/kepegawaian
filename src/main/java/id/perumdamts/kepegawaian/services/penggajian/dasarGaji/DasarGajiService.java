package id.perumdamts.kepegawaian.services.penggajian.dasarGaji;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiRequest;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DasarGajiService {
    List<DasarGajiResponse> findAll(DasarGajiRequest request);

    Page<DasarGajiResponse> findPage(DasarGajiRequest request);

    DasarGajiResponse findById(Long id);

    SavedStatus<?> save(DasarGajiPostRequest request);

    SavedStatus<?> saveBatch(List<DasarGajiPostRequest> requests);

    SavedStatus<?> update(Long id, DasarGajiPutRequest request);

    boolean deleteById(Long id);
}
