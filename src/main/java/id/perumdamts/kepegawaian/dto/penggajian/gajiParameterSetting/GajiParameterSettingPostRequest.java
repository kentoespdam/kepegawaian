package id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting;

import id.perumdamts.kepegawaian.entities.penggajian.GajiParameterSetting;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GajiParameterSettingPostRequest {
    @NotEmpty(message = "Kode is required")
    private String kode;
    @NotNull(message = "Nominal is required")
    private Double nominal;



}
