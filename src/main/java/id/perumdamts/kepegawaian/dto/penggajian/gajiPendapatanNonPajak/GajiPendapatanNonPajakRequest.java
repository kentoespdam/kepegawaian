package id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiPendapatanNonPajakRequest extends CommonPageRequest {
    private String kode;

    @JsonIgnore
    public Specification<GajiPendapatanNonPajak> getSpecification() {
        Specification<GajiPendapatanNonPajak> kodeSpec = Objects.isNull(kode) ? null :
                (root, query, cb) -> cb.equal(root.get("kode"), kode);
        return Specification.where(kodeSpec);
    }
}
