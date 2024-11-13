package id.perumdamts.kepegawaian.services.penggajian.gajiKomponen;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.*;
import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;

import java.util.List;

public interface GajiKomponenService {
    List<GajiKomponenMiniProjection> findAllKode(Long profilId);
    List<GajiKomponenResponse> findByProfil(Long id);
    GajiKomponenResponse findDetail(Long id);
    SavedStatus<?> create(GajiKomponenPostRequest request);
    SavedStatus<?> update(Long id, GajiKomponenPutRequest request);
    Boolean delete(Long id);
    void generateDefaultValue(GajiProfil profilGaji);
}
