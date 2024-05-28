package id.perumdamts.kepegawaian.services.master.alatKerja;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.alatKerja.AlatKerjaPostRequest;
import id.perumdamts.kepegawaian.dto.master.alatKerja.AlatKerjaPutRequest;
import id.perumdamts.kepegawaian.dto.master.alatKerja.AlatKerjaRequest;
import id.perumdamts.kepegawaian.dto.master.alatKerja.AlatKerjaResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AlatKerjaService {
    List<AlatKerjaResponse> findAll();

    Page<AlatKerjaResponse> findPage(AlatKerjaRequest request);

    AlatKerjaResponse findById(Long id);

    List<AlatKerjaResponse> findByProfesiId(Long id);

    SavedStatus<?> save(AlatKerjaPostRequest request);

    SavedStatus<?> saveBatch(List<AlatKerjaPostRequest> requests);

    SavedStatus<?> update(Long id, AlatKerjaPutRequest request);

    boolean deleteById(Long id);
}
