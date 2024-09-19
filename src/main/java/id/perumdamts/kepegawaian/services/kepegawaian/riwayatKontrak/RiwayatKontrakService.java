package id.perumdamts.kepegawaian.services.kepegawaian.riwayatKontrak;

import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakResponse;
import org.springframework.data.domain.Page;


public interface RiwayatKontrakService {

    Page<RiwayatKontrakResponse> findByPegawaiId(Long id, RiwayatKontrakRequest request);
}
