package id.perumdamts.kepegawaian.mapper.penggajian.gajiPhdp;

import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpPutRequest;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPhdp;

public final class GajiPhdpMapper {
    private GajiPhdpMapper() {}

    public static GajiPhdp toEntity(GajiPhdpPostRequest request) {
        GajiPhdp entity = new GajiPhdp();
        entity.setUrut(request.getUrut());
        entity.setKondisi(request.getKondisi().toUpperCase());
        entity.setFormula(request.getFormula().toUpperCase());
        return entity;
    }

    public static void updateEntity(GajiPhdp entity, GajiPhdpPutRequest request) {
        entity.setUrut(request.getUrut());
        entity.setKondisi(request.getKondisi().toUpperCase());
        entity.setFormula(request.getFormula().toUpperCase());
    }
}
