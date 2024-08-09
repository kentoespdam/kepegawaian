package id.perumdamts.kepegawaian.services.pegawai;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.pegawai.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PegawaiService {
    Page<PegawaiResponse> findPage(PegawaiRequest request);

    List<PegawaiResponse> findAll();

    PegawaiResponseDetail findById(Long id);

    SavedStatus<?> save(PegawaiPostRequest request);

    SavedStatus<?> saveBatch(List<PegawaiPostRequest> requests);

    SavedStatus<?> update(Long id, PegawaiPutRequest request);

    boolean deleteById(Long id);
}
