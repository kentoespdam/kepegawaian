package id.perumdamts.kepegawaian.dto.cuti.jenis;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class CutiJenisRequest extends CommonPageRequest {
    private Long parentId;
    private String nama;

    public Specification<CutiJenis> getSpecification() {
        Specification<CutiJenis> parentIdSpec = Objects.isNull(parentId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("parent").get("id"), parentId);
        Specification<CutiJenis> namaSpec = Objects.isNull(nama) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("nama"), "%" + nama + "%");
        return Specification.where(parentIdSpec).and(namaSpec);
    }
}
