package id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp;

import id.perumdamts.kepegawaian.entities.penggajian.GajiPhdp;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class GajiPhdpPostRequest {
    private Integer urut;
    @NotEmpty(message = "Kondisi is required")
    private String kondisi;
    @NotEmpty(message = "Formula is required")
    private String formula;



}
