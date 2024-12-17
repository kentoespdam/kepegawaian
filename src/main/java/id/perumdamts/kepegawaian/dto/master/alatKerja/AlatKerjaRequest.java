package id.perumdamts.kepegawaian.dto.master.alatKerja;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.AlatKerja;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlatKerjaRequest extends CommonPageRequest {
    private String nama;
    private Long profesiId;

    @JsonIgnore
    public Specification<AlatKerja> getSpecification() {
        Specification<AlatKerja> namaSpec = Objects.isNull(nama) ? null :
                (root, query, cb) -> cb.like(root.get("nama"), "%" + nama + "%");
        Specification<AlatKerja> profesiSpec = Objects.isNull(profesiId) ? null :
                (root, query, cb) -> cb.equal(root.get("profesi").get("id"), profesiId);

        return Specification.where(namaSpec).and(profesiSpec);
    }
}
