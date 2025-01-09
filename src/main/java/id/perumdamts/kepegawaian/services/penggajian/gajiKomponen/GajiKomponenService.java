package id.perumdamts.kepegawaian.services.penggajian.gajiKomponen;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.*;
import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GajiKomponenService {
    List<GajiKomponenMiniProjection> findAllKode(Long profilId);

    Page<GajiKomponenResponse> findByProfil(Long id, GajiKomponenRequest request);

    Integer findLastUrut(Long profilId);

    GajiKomponenResponse findDetail(Long id);

    SavedStatus<?> create(GajiKomponenPostRequest request);

    SavedStatus<?> update(Long id, GajiKomponenPutRequest request);

    Boolean delete(Long id);

    void generateDefaultValue(GajiProfil profilGaji);
}
