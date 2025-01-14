package id.perumdamts.kepegawaian.dto.master.organisasi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrganisasiRequest extends CommonPageRequest {
    private String kode;
    private String nama;
    private Long parentId;

    @JsonIgnore
    public Specification<Organisasi> getSpecification() {
        Specification<Organisasi> kodeSpec = Objects.isNull(kode) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("kode"),  kode + "%");
        Specification<Organisasi> namaSpec = Objects.isNull(nama) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("nama"), "%" + nama + "%");
        Specification<Organisasi> parentIdSpec = Objects.isNull(parentId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("parent").get("id"), parentId);
        return Specification.where(kodeSpec).and(namaSpec).and(parentIdSpec);
    }
}
