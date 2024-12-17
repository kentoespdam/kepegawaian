package id.perumdamts.kepegawaian.services.kepegawaian.riwayatKontrak;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakResponse;
import org.springframework.data.domain.Page;


public interface RiwayatKontrakService {

    Page<RiwayatKontrakResponse> findByPegawaiId(Long id, RiwayatKontrakRequest request);

    RiwayatKontrakResponse findById(Long id);

    SavedStatus<?> save(RiwayatKontrakPostRequest request);

    SavedStatus<?> update(Long id, RiwayatKontrakPutRequest request);

    boolean delete(Long id);
}
