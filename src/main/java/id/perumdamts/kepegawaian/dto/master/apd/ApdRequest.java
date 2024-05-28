package id.perumdamts.kepegawaian.dto.master.apd;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.Apd;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApdRequest extends CommonPageRequest {
    private String nama;
    private Long profesiId;

    public Specification<Apd> getSpecification() {
        Specification<Apd> namaSpec = Objects.isNull(nama) ? null : (root, query, cb) -> cb.like(root.get("nama"), "%" + nama + "%");
        Specification<Apd> profesiSpec = Objects.isNull(profesiId) ? null : (root, query, cb) -> cb.equal(root.get("profesi").get("id"), profesiId);

        return Specification.where(namaSpec).and(profesiSpec);
    }
}
