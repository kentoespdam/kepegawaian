package id.perumdamts.kepegawaian.services.profil.pelatihan;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanPostRequest;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanPutRequest;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanRequest;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PelatihanService {
    List<PelatihanResponse> findAll();
    Page<PelatihanResponse> findPage(PelatihanRequest request);
    PelatihanResponse findById(Long id);
    List<PelatihanResponse> findByBiodataId(String biodataId);
    SavedStatus<?> save(PelatihanPostRequest request);
    SavedStatus<?> update(Long id, PelatihanPutRequest request);
    SavedStatus<?> acceptPelatihan(Long id, String nik, String username);
    Boolean delete(Long id);
}
