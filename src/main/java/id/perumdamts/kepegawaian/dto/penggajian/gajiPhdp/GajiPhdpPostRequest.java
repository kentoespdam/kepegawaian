package id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPhdp;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class GajiPhdpPostRequest {
    @NotEmpty(message = "Kondisi is required")
    private String kondisi;
    @NotEmpty(message = "Formula is required")
    private String formula;

    @JsonIgnore
    public Specification<GajiPhdp> getSpecification() {
        Specification<GajiPhdp> kondisiSpec = (root, query, cb) -> cb.equal(root.get("kondisi"), kondisi);
        Specification<GajiPhdp> formulaSpec = (root, query, cb) -> cb.equal(root.get("formula"), formula);
        return Specification.where(kondisiSpec).and(formulaSpec);
    }

    public static GajiPhdp toEntity(GajiPhdpPostRequest request) {
        GajiPhdp gajiPhdp = new GajiPhdp();
        gajiPhdp.setKondisi(request.getKondisi());
        gajiPhdp.setFormula(request.getFormula());
        return gajiPhdp;
    }
}
