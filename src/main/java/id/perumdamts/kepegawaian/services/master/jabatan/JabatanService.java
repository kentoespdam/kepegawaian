package id.perumdamts.kepegawaian.services.master.jabatan;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanPostRequest;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanPutRequest;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanRequest;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JabatanService {
    List<JabatanResponse> findAll();
    Page<JabatanResponse> findPage(JabatanRequest request);
    JabatanResponse findById(Long id);
    SavedStatus<?> save(JabatanPostRequest request);
    SavedStatus<?> saveBatch(List<JabatanPostRequest> requests);
    SavedStatus<?> update(Long id, JabatanPutRequest request);
    Boolean deleteById(Long id);
}
