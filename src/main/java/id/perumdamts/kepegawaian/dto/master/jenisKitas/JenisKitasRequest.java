package id.perumdamts.kepegawaian.dto.master.jenisKitas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.JenisKitas;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@Data
@EqualsAndHashCode(callSuper = true)
public class JenisKitasRequest extends CommonPageRequest {
    private String nama;

    @JsonIgnore
    public Specification<JenisKitas> getSpecification() {
        Specification<JenisKitas> namaSpec= Objects.isNull(nama) ? null : (root, query, cb) -> cb.like(root.get("nama"), "%" + nama + "%");

        return Specification.where(namaSpec);
    }
}
