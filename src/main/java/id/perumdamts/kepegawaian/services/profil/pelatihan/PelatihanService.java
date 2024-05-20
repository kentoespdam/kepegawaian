package id.perumdamts.kepegawaian.services.profil.pelatihan;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilResponse;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.*;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

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

    // Lampiran
    List<LampiranProfilResponse> getLampiran(Long id);
    LampiranProfilResponse getLampiranDetail(Long id);
    ResponseEntity<?> getFileLampiranById(Long id);
    SavedStatus<?> addLampiran(PelatihanLampiranPostRequest request);
    Boolean deleteLampiran(Long id);
}
