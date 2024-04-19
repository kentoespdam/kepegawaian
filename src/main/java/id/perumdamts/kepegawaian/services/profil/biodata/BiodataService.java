package id.perumdamts.kepegawaian.services.profil.biodata;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPostRequest;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPutRequest;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataRequest;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BiodataService {
    List<BiodataResponse> findAll();
    Page<BiodataResponse> findPage(BiodataRequest request);
    BiodataResponse findById(String id);
    SavedStatus<?> save(BiodataPostRequest request);
    SavedStatus<?> update(String id, BiodataPutRequest request);
    Boolean deleteById(String id);
}
