package id.perumdamts.kepegawaian.services.penggajian.gajiProfil;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GajiProfilService {

    Page<GajiProfilResponse> findAll();

    List<GajiProfilResponse> list();

    GajiProfilResponse findById(Long id);

    SavedStatus<?> create(GajiProfilPostRequest request);

    SavedStatus<?> update(Long id, GajiProfilPutRequest request);

    boolean delete(Long id);
}
