package id.perumdamts.kepegawaian.dto.master.jenisPelatihan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.JenisPelatihan;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class JenisPelatihanRequest extends CommonPageRequest {
    private String nama;

    @JsonIgnore
    public Specification<JenisPelatihan> getSpecification() {
        return SpecificationBuilder.<JenisPelatihan>of()
                .addLike(nama, "nama")
                .build();
    }
}
