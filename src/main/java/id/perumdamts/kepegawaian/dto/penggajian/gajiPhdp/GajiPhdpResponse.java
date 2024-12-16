package id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp;

import id.perumdamts.kepegawaian.entities.penggajian.GajiPhdp;
import lombok.Data;

@Data
public class GajiPhdpResponse {
    private Long id;
    private Integer urut;
    private String kondisi;
    private String formula;

    public static GajiPhdpResponse from(GajiPhdp entity) {
        GajiPhdpResponse response = new GajiPhdpResponse();
        response.setId(entity.getId());
        response.setUrut(entity.getUrut());
        response.setKondisi(entity.getKondisi());
        response.setFormula(entity.getFormula());
        return response;
    }
}
