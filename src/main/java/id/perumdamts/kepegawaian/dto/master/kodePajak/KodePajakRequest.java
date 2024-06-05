package id.perumdamts.kepegawaian.dto.master.kodePajak;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.KodePajak;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class KodePajakRequest extends CommonPageRequest {
    private String kode;
    private Double nominal;

    @JsonIgnore
    public Specification<KodePajak> getSpecification() {
        Specification<KodePajak> kodeSpec = Objects.isNull(kode) ? null :
                (root, query, cb) -> cb.equal(root.get("kode"), kode);
        return Specification.where(kodeSpec);
    }
}
