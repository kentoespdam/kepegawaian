package id.perumdamts.kepegawaian.services.pegawai;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.pegawai.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PegawaiService {
    Page<PegawaiResponse> findPage(PegawaiRequest request);

    List<PegawaiListResponse> findAll(PegawaiRequest request);

    PegawaiResponseDetail findById(Long id);

    PegawaiResponseRingkasan findRingkasan(Long id);

    SavedStatus<?> save(PegawaiPostRequest request);

    SavedStatus<?> saveBatch(List<PegawaiPostRequest> requests);

    SavedStatus<?> update(Long id, PegawaiPutRequest request);

    SavedStatus<?> patchGaji(Long id, PegawaiPatchGaji request);

    boolean deleteById(Long id);
}
