package id.perumdamts.kepegawaian.dto.master.profesi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProfesiRequest extends CommonPageRequest {
    private Long organisasiId;
    private Long jabatanId;
    private Long levelId;
    private Long gradeId;
    private String nama;

    @JsonIgnore
    public Specification<Profesi> getSpecification() {
        Specification<Profesi> organisasiSpec = Objects.isNull(organisasiId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("organisasi").get("id"), organisasiId);
        Specification<Profesi> levelSpec = Objects.isNull(levelId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("level").get("id"), levelId);
        Specification<Profesi> namaSpec = Objects.isNull(nama) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("nama"), "%" + nama + "%");
        Specification<Profesi> jabatanSpec = Objects.isNull(jabatanId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("jabatan").get("id"), jabatanId);
        Specification<Profesi> gradeSpec = Objects.isNull(gradeId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("grade").get("id"), gradeId);

        return Specification.where(organisasiSpec).and(levelSpec).and(namaSpec).and(jabatanSpec).and(gradeSpec);
    }
}
