package id.perumdamts.kepegawaian.dto.master.statusKerja;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.StatusKerja;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class StatusKerjaRequest extends CommonPageRequest {
    private String nama;

    public Specification<StatusKerja> getSpecification() {
        Specification<StatusKerja> namaSpec = Objects.isNull(nama) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("nama"), "%" + nama + "%");

        return Specification.where(namaSpec);
    }
}
