package id.perumdamts.kepegawaian.services.profil.keahlian;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.keahlian.*;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface KeahlianService {
    List<KeahlianResponse> findAll();
    Page<KeahlianResponse> findPage(KeahlianRequest request);
    KeahlianResponse findById(Long id);
    Page<KeahlianResponse> findByBiodataId(String biodataId, KeahlianRequest request);
    SavedStatus<?> save(KeahlianPostRequest request);
    SavedStatus<?> update(Long id, KeahlianPutRequest request);
    SavedStatus<?> acceptKeahlian(Long id, String nik, String username);
    Boolean delete(Long id);

    //Lampiran
    List<LampiranProfilResponse> getLampiran(Long id);
    LampiranProfilResponse getLampiranById(Long id);
    ResponseEntity<?> getFileLampiranById(Long id);
    SavedStatus<?> addLampiran(KeahlianLampiranPostRequest request);
    Boolean deleteLampiran(Long id);
}
