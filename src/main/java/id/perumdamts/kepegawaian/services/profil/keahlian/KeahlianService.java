package id.perumdamts.kepegawaian.services.profil.keahlian;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianPostRequest;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianPutRequest;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianRequest;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface KeahlianService {
    List<KeahlianResponse> findAll();
    Page<KeahlianResponse> findPage(KeahlianRequest request);
    KeahlianResponse findById(Long id);
    List<KeahlianResponse> findByBiodataId(String biodataId);
    SavedStatus<?> save(KeahlianPostRequest request);
    SavedStatus<?> update(Long id, KeahlianPutRequest request);
    SavedStatus<?> acceptKeahlian(Long id, String nik, String username);
    Boolean delete(Long id);
}
