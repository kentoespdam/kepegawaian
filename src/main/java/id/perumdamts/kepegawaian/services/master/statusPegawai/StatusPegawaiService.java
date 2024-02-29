package id.perumdamts.kepegawaian.services.master.statusPegawai;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.statusPegawai.StatusPegawaiPostRequest;
import id.perumdamts.kepegawaian.dto.master.statusPegawai.StatusPegawaiPutRequest;
import id.perumdamts.kepegawaian.dto.master.statusPegawai.StatusPegawaiRequest;
import id.perumdamts.kepegawaian.dto.master.statusPegawai.StatusPegawaiResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StatusPegawaiService {
    List<StatusPegawaiResponse> findAll();
    Page<StatusPegawaiResponse> findPage(StatusPegawaiRequest request);
    StatusPegawaiResponse findById(Long id);
    SavedStatus<?> save(StatusPegawaiPostRequest request);
    SavedStatus<?> saveBatch(List<StatusPegawaiPostRequest> requests);
    SavedStatus<?> update(Long id, StatusPegawaiPutRequest request);
    Boolean deleteById(Long id);
}
