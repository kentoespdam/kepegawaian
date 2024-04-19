package id.perumdamts.kepegawaian.services.profil.kartuIdentitas;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasPostRequest;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasPutRequest;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasRequest;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasResponse;
import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;
import org.springframework.data.domain.Page;

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
}
