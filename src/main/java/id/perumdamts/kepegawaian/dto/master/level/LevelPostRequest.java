package id.perumdamts.kepegawaian.dto.master.level;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class LevelPostRequest {
    private String nama;

    @JsonIgnore
    public Specification<Level> getSpecification() {
        return SpecificationBuilder.<Level>of()
                .addEqual(nama, "nama")
                .build();
    }

}
