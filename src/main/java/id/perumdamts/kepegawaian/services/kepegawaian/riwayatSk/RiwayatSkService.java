package id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkResponse;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiPostRequest;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RiwayatSkService {
    List<RiwayatSkResponse> findAll(RiwayatSkRequest request);
    Page<RiwayatSkResponse> findPage(RiwayatSkRequest request);
    RiwayatSkResponse findById(Long id);
    List<RiwayatSkResponse> findByIds(List<Long> riwayatIds);
    List<RiwayatSkResponse> findByPegawai(Long pegawaiId);
    Page<RiwayatSkResponse> findByPegawaiId(Long pegawaiId, RiwayatSkRequest request);
    SavedStatus<?> save(RiwayatSkPostRequest request);
    RiwayatSk saveCapeg(PegawaiPostRequest request, Pegawai pegawai);
    RiwayatSk savePegawai(PegawaiPostRequest request, Pegawai pegawai);
    SavedStatus<?> update(Long id, RiwayatSkPutRequest request);
    Boolean delete(Long id);
}
