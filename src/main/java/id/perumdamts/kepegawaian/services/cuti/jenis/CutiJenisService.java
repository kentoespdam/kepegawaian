package id.perumdamts.kepegawaian.services.cuti.jenis;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisPutRequest;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisRequest;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CutiJenisService {
    Page<CutiJenisResponse> findPage(CutiJenisRequest request);
    List<CutiJenisResponse> findList(CutiJenisRequest request);
    CutiJenisResponse findById(Long id);
    SavedStatus<?> save(CutiJenisPostRequest request);
    SavedStatus<?> update(Long id, CutiJenisPutRequest request);
    boolean delete(Long id);
}
