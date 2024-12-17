package id.perumdamts.kepegawaian.services.penggajian.gajiKomponen;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.GajiKomponenMiniProjection;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.GajiKomponenPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.GajiKomponenPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.GajiKomponenResponse;
import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GajiKomponenService {
    List<GajiKomponenMiniProjection> findAllKode(Long profilId);

    Page<GajiKomponenResponse> findByProfil(Long id, CommonPageRequest request);

    Integer findLastUrut(Long profilId);

    GajiKomponenResponse findDetail(Long id);

    SavedStatus<?> create(GajiKomponenPostRequest request);

    SavedStatus<?> update(Long id, GajiKomponenPutRequest request);

    Boolean delete(Long id);

    void generateDefaultValue(GajiProfil profilGaji);
}
