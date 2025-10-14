package id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.penggajian.GajiKomponen;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiKomponenRequest extends CommonPageRequest {
    private Long profilId;
    private String kode;

    @JsonIgnore
    public Specification<GajiKomponen> getSpecification() {
        return SpecificationBuilder.<GajiKomponen>of()
                .addEqual(profilId, "profilGaji", "id")
                .addEqual(kode, "kode")
                .build();
    }
}
