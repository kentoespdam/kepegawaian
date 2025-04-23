package id.perumdamts.kepegawaian.dto.master.sanksi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.Sanksi;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class SanksiRequest extends CommonPageRequest {
    private String kode;
    private String nama;

    @JsonIgnore
    public Specification<Sanksi> getSpecification() {
        Specification<Sanksi> kodeSpec = Objects.isNull(kode) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("kode"), kode);
        Specification<Sanksi> namaSpec = Objects.isNull(nama) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("nama"), nama);
        return Specification.where(kodeSpec).and(namaSpec);
    }
}
