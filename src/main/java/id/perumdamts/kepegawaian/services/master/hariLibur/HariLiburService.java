package id.perumdamts.kepegawaian.services.master.hariLibur;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburPostRequest;
import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburPutRequest;
import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburRequest;
import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburResponse;
import org.springframework.data.domain.Page;

public interface HariLiburService {
    Page<HariLiburResponse> findPage(HariLiburRequest request);
    HariLiburResponse findById(Long id);
    SavedStatus<?> save(HariLiburPostRequest request);
    SavedStatus<?> update(Long id, HariLiburPutRequest request);
    Boolean delete(Long id);
}
