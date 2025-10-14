package id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiPendapatanNonPajakRequest extends CommonPageRequest {
    private String kode;

    @JsonIgnore
    public Specification<GajiPendapatanNonPajak> getSpecification() {
        return SpecificationBuilder.<GajiPendapatanNonPajak>of()
                .addEqual(kode, "kode")
                .build();
    }
}
