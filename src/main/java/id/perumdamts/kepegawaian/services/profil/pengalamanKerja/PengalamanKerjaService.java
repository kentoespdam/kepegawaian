package id.perumdamts.kepegawaian.services.profil.pengalamanKerja;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilResponse;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.*;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PengalamanKerjaService {
    List<PengalamanKerjaResponse> findAll();
    Page<PengalamanKerjaResponse> findPage(PengalamanKerjaRequest request);
    PengalamanKerjaResponse findById(Long id);
    List<PengalamanKerjaResponse> findByBiodataId(String biodataId);
    SavedStatus<?> save(PengalamanKerjaPostRequest request);
    SavedStatus<?> update(Long id, PengalamanKerjaPutRequest request);
    SavedStatus<?> acceptPengalamanKerja(Long id, PengalamanKerjaAcceptRequest request, String username);
    Boolean deleteById(Long id);

    // Lampiran
    List<LampiranProfilResponse> getLampiran(Long id);
    LampiranProfilResponse getLampiranById(Long id);
    ResponseEntity<?> getFileLampiranById(Long id);
    SavedStatus<?> addLampiran(PengalamanLampiranPostRequest request);
    Boolean deleteLampiran(Long id);
}
