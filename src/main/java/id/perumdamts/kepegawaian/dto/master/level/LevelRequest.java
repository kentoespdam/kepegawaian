package id.perumdamts.kepegawaian.dto.master.level;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class LevelRequest extends CommonPageRequest {
    private String nama;

    @JsonIgnore
    public Specification<Level> getSpecification() {
        return SpecificationBuilder.<Level>of()
                .addLike(nama, "nama")
                .build();
    }
}
