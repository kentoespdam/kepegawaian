package id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen;

import id.perumdamts.kepegawaian.entities.penggajian.GajiKomponen;
import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;

public class GajiKomponenPutRequest extends GajiKomponenPostRequest {
    public static GajiKomponen toEntity(GajiKomponen entity, GajiKomponenPutRequest request, GajiProfil profilGaji) {
        entity.setUrut(request.getUrut());
        entity.setProfilGaji(profilGaji);
        entity.setKode(request.getKode().toUpperCase());
        entity.setNama(request.getNama());
        entity.setJenisGaji(request.getJenisGaji());
        entity.setNilai(request.getNilai());
        entity.setIsReference(request.getIsReference());
        if (request.getIsReference())
            entity.setFormula("#SYSTEM");
        else
            entity.setFormula(request.getFormula());
        return entity;
    }
}
