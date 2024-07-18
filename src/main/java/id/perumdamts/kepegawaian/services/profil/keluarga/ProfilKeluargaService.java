package id.perumdamts.kepegawaian.services.profil.keluarga;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.keluarga.*;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProfilKeluargaService {
    List<ProfilKeluargaResponse> findAll();
    Page<ProfilKeluargaResponse> findPage(ProfilKeluargaRequest request);
    ProfilKeluargaResponse findById(Long id);
    Page<ProfilKeluargaResponse> findByBiodataId(String biodataId, ProfilKeluargaRequest request);
    SavedStatus<?> save(ProfilKeluargaPostRequest request);
    SavedStatus<?> update(Long id, ProfilKeluargaPutRequest request);
    Boolean delete(Long id);

    // Lampiran
    List<LampiranProfilResponse> getLampiran(Long id);
    LampiranProfilResponse getLampiranById(Long id);
    ResponseEntity<?> getFileLampiranById(Long id);
    SavedStatus<?> addLampiran(ProfilKeluargaLampiranPostRequest request);
    Boolean deleteLampiran(Long id);
}
