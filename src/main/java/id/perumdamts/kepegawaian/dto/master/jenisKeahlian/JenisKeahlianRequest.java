package id.perumdamts.kepegawaian.dto.master.jenisKeahlian;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.JenisKeahlian;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class JenisKeahlianRequest extends CommonPageRequest {
    private String nama;

    @JsonIgnore
    public Specification<JenisKeahlian> getSpecification() {
        Specification<JenisKeahlian> namaSpec= Objects.isNull(nama) ? null : (root, query, cb) -> cb.like(root.get("nama"), "%" + nama + "%");

        return Specification.where(namaSpec);
    }
}
