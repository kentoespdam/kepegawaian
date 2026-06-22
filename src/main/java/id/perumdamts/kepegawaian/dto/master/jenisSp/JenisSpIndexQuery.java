package id.perumdamts.kepegawaian.dto.master.jenisSp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import id.perumdamts.kepegawaian.entities.master.JenisSp;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class JenisSpIndexQuery extends PagedRequest {
    private String kode;
    private String nama;

    @JsonIgnore
    public Specification<JenisSp> getSpecification() {
        return SpecificationBuilder.<JenisSp>of()
                .addLike(kode, "kode")
                .addLike(nama, "nama")
                .build();
    }
}
