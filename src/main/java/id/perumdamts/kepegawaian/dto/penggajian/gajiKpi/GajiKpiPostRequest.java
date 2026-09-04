package id.perumdamts.kepegawaian.dto.penggajian.gajiKpi;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class GajiKpiPostRequest {
    @NotEmpty(message = "Nipam is required")
    private String nipam;
    @NotEmpty(message = "Periode is required")
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Periode harus format YYYY-MM")
    private String periode;
    @NotNull(message = "Tunkin is required")
    private Double tunkin;
    private Double pph21Ter;
}
