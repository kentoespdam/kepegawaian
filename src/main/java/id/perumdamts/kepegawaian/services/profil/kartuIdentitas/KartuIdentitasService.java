package id.perumdamts.kepegawaian.services.profil.kartuIdentitas;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.*;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilResponse;
import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface KartuIdentitasService {
    List<KartuIdentitasResponse> findAll();
    Page<KartuIdentitasResponse> findPage(KartuIdentitasRequest request);
    KartuIdentitasResponse findById(Long id);
    List<KartuIdentitasResponse> findByNik(String nik);
    KartuIdentitas execSave(KartuIdentitas kartuIdentitas);
    SavedStatus<?> save(KartuIdentitasPostRequest request);
    SavedStatus<?> update(Long id, KartuIdentitasPutRequest request);
    Boolean deleteById(Long id);

    // Lampiran
    List<LampiranProfilResponse> getLampiran(Long id);
    LampiranProfilResponse getLampiranById(Long id);
    ResponseEntity<?> getFileLampiranById(Long id);
    SavedStatus<?> addLampiran(KartuIdentitasLampiranPostRequest request);
    Boolean deleteLampiran(Long id);
}
