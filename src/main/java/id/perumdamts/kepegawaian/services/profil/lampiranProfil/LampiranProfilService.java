package id.perumdamts.kepegawaian.services.profil.lampiranProfil;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilPostRequest;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LampiranProfilService {
    List<LampiranProfilResponse> getLampiran(EJenisLampiranProfil eJenisLampiranProfil, Long id);

    LampiranProfilResponse getLampiranById(Long id);

    ResponseEntity<?> getFileLampiranById(EJenisLampiranProfil ref, Long id);

    SavedStatus<?> addLampiran(LampiranProfilPostRequest request);

    boolean deleteById(Long id);
}
