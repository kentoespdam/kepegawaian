package id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.penggajian.DetailDasarGaji;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class DetailDasarGajiRequest extends CommonPageRequest {
    private Long dasarGajiId;
    private Integer mkg;
    private Integer golonganKode;
    private Integer nominal;

    @JsonIgnore
    public Specification<DetailDasarGaji> getSpecification() {
        return SpecificationBuilder.<DetailDasarGaji>of()
                .addEqual(dasarGajiId, "dasarGaji", "id")
                .addEqual(mkg, "mkg")
                .addEqual(golonganKode, "golonganKode")
                .addEqual(nominal, "nominal")
                .build();
    }
}
