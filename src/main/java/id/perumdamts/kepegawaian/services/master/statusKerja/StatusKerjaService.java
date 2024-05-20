package id.perumdamts.kepegawaian.services.master.statusKerja;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.statusKerja.StatusKerjaPostRequest;
import id.perumdamts.kepegawaian.dto.master.statusKerja.StatusKerjaPutRequest;
import id.perumdamts.kepegawaian.dto.master.statusKerja.StatusKerjaRequest;
import id.perumdamts.kepegawaian.dto.master.statusKerja.StatusKerjaResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StatusKerjaService {
    List<StatusKerjaResponse> findAll();
    Page<StatusKerjaResponse> findPage(StatusKerjaRequest request);
    StatusKerjaResponse findById(Long id);
    SavedStatus<?> save(StatusKerjaPostRequest request);
    SavedStatus<?> saveBatch(List<StatusKerjaPostRequest> requests);
    SavedStatus<?> update(Long id, StatusKerjaPutRequest request);
    Boolean deleteById(Long id);
}
