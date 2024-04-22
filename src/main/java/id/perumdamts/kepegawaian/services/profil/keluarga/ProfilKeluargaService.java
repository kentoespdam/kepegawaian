package id.perumdamts.kepegawaian.services.profil.keluarga;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaPostRequest;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaPutRequest;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaRequest;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProfilKeluargaService {
    List<ProfilKeluargaResponse> findAll();
    Page<ProfilKeluargaResponse> findPage(ProfilKeluargaRequest request);
    ProfilKeluargaResponse findById(Long id);
    List<ProfilKeluargaResponse> findByBiodataId(String biodataId);
    SavedStatus<?> save(ProfilKeluargaPostRequest request);
    SavedStatus<?> update(Long id, ProfilKeluargaPutRequest request);
    Boolean delete(Long id);
}
