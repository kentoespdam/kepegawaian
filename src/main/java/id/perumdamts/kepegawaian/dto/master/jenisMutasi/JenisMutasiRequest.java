package id.perumdamts.kepegawaian.dto.master.jenisMutasi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.JenisMutasi;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class JenisMutasiRequest extends CommonPageRequest {
    private String nama;
    private String notes;

    @JsonIgnore
    public Specification<JenisMutasi> getSpecification() {
        Specification<JenisMutasi> namaSpec = Objects.isNull(nama) ? null :
                (root, query, cb) -> cb.like(root.get("nama"), "%" + nama + "%");
        return Specification.where(namaSpec);
    }
}
