package id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp;

import id.perumdamts.kepegawaian.entities.penggajian.GajiPhdp;

public class GajiPhdpPutRequest extends GajiPhdpPostRequest {
    public static GajiPhdp toEntity(GajiPhdp entity, GajiPhdpPutRequest request) {
        entity.setUrut(request.getUrut());
        entity.setKondisi(request.getKondisi().toUpperCase());
        entity.setFormula(request.getFormula().toUpperCase());
        return entity;
    }
}
