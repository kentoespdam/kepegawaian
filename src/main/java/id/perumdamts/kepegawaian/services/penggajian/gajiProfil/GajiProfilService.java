package id.perumdamts.kepegawaian.services.penggajian.gajiProfil;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilResponse;

import java.util.List;

public interface GajiProfilService {

    List<GajiProfilResponse> findAll();

    GajiProfilResponse findById(Long id);

    SavedStatus<?> create(GajiProfilPostRequest request);

    SavedStatus<?> update(Long id, GajiProfilPutRequest request);

    boolean delete(Long id);
}
