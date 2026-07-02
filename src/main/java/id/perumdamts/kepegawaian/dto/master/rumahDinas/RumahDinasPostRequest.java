package id.perumdamts.kepegawaian.dto.master.rumahDinas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.RumahDinas;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class RumahDinasPostRequest {
    private String nama;
    private Double nilai;

    @JsonIgnore
    public Specification<RumahDinas> getSpecification() {
        return SpecificationBuilder.<RumahDinas>of()
                .addEqual(nama, "nama")
                .build();
    }

}
