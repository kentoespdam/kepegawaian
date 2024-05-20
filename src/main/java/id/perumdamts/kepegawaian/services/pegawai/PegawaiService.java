package id.perumdamts.kepegawaian.services.pegawai;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiPostRequest;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiPutRequest;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiRequest;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PegawaiService {
    Page<PegawaiResponse> findPage(PegawaiRequest request);

    List<PegawaiResponse> findAll();

    PegawaiResponse findById(Long id);

    SavedStatus<?> save(PegawaiPostRequest request);

    SavedStatus<?> saveBatch(List<PegawaiPostRequest> requests);

    SavedStatus<?> update(Long id, PegawaiPutRequest request);

    boolean deleteById(Long id);
}
