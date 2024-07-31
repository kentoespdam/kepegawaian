package id.perumdamts.kepegawaian.services.penggajian.detailDasarGaji;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiRequest;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DetailDasarGajiService {
    List<DetailDasarGajiResponse> findAll(DetailDasarGajiRequest request);

    Page<DetailDasarGajiResponse> findPage(DetailDasarGajiRequest request);

    DetailDasarGajiResponse findById(Long id);

    SavedStatus<?> save(DetailDasarGajiPostRequest request);

    SavedStatus<?> saveBatch(List<DetailDasarGajiPostRequest> requests);

    SavedStatus<?> update(Long id, DetailDasarGajiPutRequest request);

    boolean deleteById(Long id);
}
