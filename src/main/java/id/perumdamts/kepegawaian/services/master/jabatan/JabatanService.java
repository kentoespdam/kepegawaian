package id.perumdamts.kepegawaian.services.master.jabatan;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.jabatan.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JabatanService {
    List<JabatanResponse> findAll();
    List<JabatanMiniResponse> findByOrganisasiId(Long id);
    Page<JabatanResponse> findPage(JabatanRequest request);
    JabatanResponse findById(Long id);
    SavedStatus<?> save(JabatanPostRequest request);
    SavedStatus<?> saveBatch(List<JabatanPostRequest> requests);
    SavedStatus<?> update(Long id, JabatanPutRequest request);
    Boolean deleteById(Long id);
}
