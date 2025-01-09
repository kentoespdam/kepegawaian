package id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.penggajian.GajiKomponen;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiKomponenRequest extends CommonPageRequest {
    private Long profilId;
    private String kode;

    @JsonIgnore
    public Specification<GajiKomponen> getSpecification() {
        Specification<GajiKomponen> profilIdSpec = Objects.isNull(profilId) ? null :
                (root, query, cb) -> cb.equal(root.get("profilGaji").get("id"), profilId);
        Specification<GajiKomponen> kodeSpec = Objects.isNull(kode) ? null :
                (root, query, cb) -> cb.equal(root.get("kode"), kode);
        return Specification.where(profilIdSpec).and(kodeSpec);
    }
}
