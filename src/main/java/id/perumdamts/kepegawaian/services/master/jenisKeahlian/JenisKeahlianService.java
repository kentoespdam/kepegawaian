package id.perumdamts.kepegawaian.services.master.jenisKeahlian;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.jenisKeahlian.JenisKeahlianPostRequest;
import id.perumdamts.kepegawaian.dto.master.jenisKeahlian.JenisKeahlianPutRequest;
import id.perumdamts.kepegawaian.dto.master.jenisKeahlian.JenisKeahlianRequest;
import id.perumdamts.kepegawaian.dto.master.jenisKeahlian.JenisKeahlianResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JenisKeahlianService {
    List<JenisKeahlianResponse> findAll();
    Page<JenisKeahlianResponse> findPage(JenisKeahlianRequest request);
    JenisKeahlianResponse findById(Long id);
    SavedStatus<?> save(JenisKeahlianPostRequest request);
    SavedStatus<?> saveBatch(List<JenisKeahlianPostRequest> requests);
    SavedStatus<?> update(Long id, JenisKeahlianPutRequest request);
    Boolean deleteById(Long id);
}
