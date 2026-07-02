package id.perumdamts.kepegawaian.mapper.penggajian.gajiKomponen;

import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.GajiKomponenPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.GajiKomponenPutRequest;
import id.perumdamts.kepegawaian.entities.penggajian.GajiKomponen;
import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;

public final class GajiKomponenMapper {
    private GajiKomponenMapper() {}

    public static GajiKomponen toEntity(GajiKomponenPostRequest request, GajiProfil profilGaji) {
        GajiKomponen entity = new GajiKomponen();
        entity.setUrut(request.getUrut());
        entity.setProfilGaji(profilGaji);
        entity.setKode(request.getKode().toUpperCase());
        entity.setNama(request.getNama());
        entity.setJenisGaji(request.getJenisGaji());
        entity.setNilai(request.getNilai());
        entity.setIsReference(request.getIsReference());
        entity.setFormula(request.getFormula());
        return entity;
    }

    public static void updateEntity(GajiKomponen entity, GajiKomponenPutRequest request, GajiProfil profilGaji) {
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
    }
}
