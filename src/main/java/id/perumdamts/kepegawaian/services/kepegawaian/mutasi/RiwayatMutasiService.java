package id.perumdamts.kepegawaian.services.kepegawaian.mutasi;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RiwayatMutasiService {
    List<RiwayatMutasiResponse> findAll(RiwayatMutasiRequest request);
    Page<RiwayatMutasiResponse> findPage(RiwayatMutasiRequest request);
    RiwayatMutasiResponse findById(Long id);
    Page<RiwayatMutasiResponse> findByPegawaiId(Long pegawaiId, RiwayatMutasiRequest request);
    SavedStatus<?> save(RiwayatMutasiPostRequest request);
    SavedStatus<?> update(Long id, RiwayatMutasiPutRequest request);

    Boolean delete(Long id);
}
