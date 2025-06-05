package id.perumdamts.kepegawaian.services.master.hariLibur;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburPostRequest;
import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburPutRequest;
import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburRequest;
import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface HariLiburService {
    Page<HariLiburResponse> findPage(HariLiburRequest request);
    List<HariLiburResponse> findList(HariLiburRequest request);
    HariLiburResponse findById(Long id);
    SavedStatus<?> save(HariLiburPostRequest request);
    SavedStatus<?> update(Long id, HariLiburPutRequest request);

    Boolean delete(Long id);
}
