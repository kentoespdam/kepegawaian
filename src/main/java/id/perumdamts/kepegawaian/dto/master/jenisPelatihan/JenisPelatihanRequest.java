package id.perumdamts.kepegawaian.dto.master.jenisPelatihan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.JenisPelatihan;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class JenisPelatihanRequest extends CommonPageRequest {
    private String nama;

    @JsonIgnore
    public Specification<JenisPelatihan> getSpecification() {
        Specification<JenisPelatihan> namaSpec= Objects.isNull(nama) ? null : (root, query, cb) -> cb.like(root.get("nama"), "%" + nama + "%");

        return Specification.where(namaSpec);
    }
}
