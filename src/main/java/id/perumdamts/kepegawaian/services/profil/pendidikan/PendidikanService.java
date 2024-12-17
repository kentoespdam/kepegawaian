package id.perumdamts.kepegawaian.services.profil.pendidikan;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilResponse;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.*;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PendidikanService {
    List<PendidikanResponse> findAll();

    Page<PendidikanResponse> findPage(PendidikanRequest request);

    PendidikanResponse findById(Long id);

    Page<PendidikanResponse> findByBiodataId(String biodataId, PendidikanRequest request);

    SavedStatus<?> save(PendidikanPostRequest request);

    SavedStatus<?> update(Long id, PendidikanPutRequest request);

    SavedStatus<?> acceptPendidikan(Long id, PendidikanAcceptRequest request, String username);

    Boolean delete(Long id);

    // Lampiran
    List<LampiranProfilResponse> getLampiran(Long id);

    LampiranProfilResponse getLampiranById(Long id);

    ResponseEntity<?> getFileLampiranById(Long id);

    SavedStatus<?> addLampiran(PendidikanLampiranPostRequest request);

    Boolean deleteLampiran(Long id);

    void saveFromBio(Biodata save, JenjangPendidikan jenjangPendidikan);
}
