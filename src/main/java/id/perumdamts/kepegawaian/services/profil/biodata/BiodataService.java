package id.perumdamts.kepegawaian.services.profil.biodata;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.biodata.*;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BiodataService {
    List<BiodataResponse> findAll();

    Page<BiodataResponse> findPage(BiodataRequest request);

    BiodataResponse findById(String id);

    BiodataResponse findByPegawaiId(Long id);

    SavedStatus<?> save(BiodataPostRequest request);

    Biodata saveFromPegawai(BiodataPostRequest request);

    SavedStatus<?> update(String id, BiodataPutRequest request);

    SavedStatus<?> patchBiodata(String id, BiodataPatchRequest request);

    SavedStatus<?> updateFotoProfil(String id, MultipartFile fileName);

    ResponseEntity<?> findFotoProfil(String id);

    Boolean deleteById(String id);
}
