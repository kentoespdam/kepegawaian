package id.perumdamts.kepegawaian.dto.master.jenisSp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.JenisSp;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class JenisSpRequest extends CommonPageRequest {
    private String kode;
    private String nama;

    @JsonIgnore
    public Specification<JenisSp> getSpecification() {
        Specification<JenisSp> kodeSpec = Objects.isNull(kode) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("kode"), "%" + kode + "%");
        Specification<JenisSp> namaSpec = Objects.isNull(nama) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("nama"), "%" + nama + "%");

        return Specification.where(kodeSpec).and(namaSpec);


    }
}
