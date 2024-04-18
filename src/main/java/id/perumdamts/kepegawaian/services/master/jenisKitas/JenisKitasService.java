package id.perumdamts.kepegawaian.services.master.jenisKitas;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.jenisKitas.JenisKitasPostRequest;
import id.perumdamts.kepegawaian.dto.master.jenisKitas.JenisKitasPutRequest;
import id.perumdamts.kepegawaian.dto.master.jenisKitas.JenisKitasRequest;
import id.perumdamts.kepegawaian.dto.master.jenisKitas.JenisKitasResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JenisKitasService {
    List<JenisKitasResponse> findAll();
    Page<JenisKitasResponse> findPage(JenisKitasRequest request);
    JenisKitasResponse findById(Long id);
    SavedStatus<?> save(JenisKitasPostRequest request);
    SavedStatus<?> saveBatch(List<JenisKitasPostRequest> requests);
    SavedStatus<?> update(Long id, JenisKitasPutRequest request);
    Boolean deleteById(Long id);
}
