package id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPhdp;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class GajiPhdpPostRequest {
    private Integer urut;
    @NotEmpty(message = "Kondisi is required")
    private String kondisi;
    @NotEmpty(message = "Formula is required")
    private String formula;

    @JsonIgnore
    public Specification<GajiPhdp> getSpecification() {
        return SpecificationBuilder.<GajiPhdp>of()
                .addEqual(kondisi, "kondisi")
                .addEqual(formula, "formula")
                .build();
    }

    public static GajiPhdp toEntity(GajiPhdpPostRequest request) {
        GajiPhdp gajiPhdp = new GajiPhdp();
        gajiPhdp.setUrut(request.getUrut());
        gajiPhdp.setKondisi(request.getKondisi().toUpperCase());
        gajiPhdp.setFormula(request.getFormula().toUpperCase());
        return gajiPhdp;
    }
}
