package id.perumdamts.kepegawaian.services.kepegawaian.terminasi;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiResponse;
import org.springframework.data.domain.Page;

public interface RiwayatTerminasiService {
    Page<RiwayatTerminasiResponse> findPage(RiwayatTerminasiRequest request);
    RiwayatTerminasiResponse findById(Long id);
    SavedStatus<?> save(RiwayatTerminasiPostRequest request);
    SavedStatus<?> update(Long id, RiwayatTerminasiPutRequest request);
}
