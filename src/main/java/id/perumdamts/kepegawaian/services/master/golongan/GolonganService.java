package id.perumdamts.kepegawaian.services.master.golongan;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganPostRequest;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganPutRequest;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganRequest;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GolonganService {
    List<GolonganResponse> findAll();
    Page<GolonganResponse> findPage(GolonganRequest request);
    GolonganResponse findById(Long id);
    SavedStatus<?> save(GolonganPostRequest request);
    SavedStatus<?> saveBatch(List<GolonganPostRequest> requests);
    SavedStatus<?> update(Long id, GolonganPutRequest request);
    Boolean deleteById(Long id);
}
