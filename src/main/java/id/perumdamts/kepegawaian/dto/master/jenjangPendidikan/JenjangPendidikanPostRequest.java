package id.perumdamts.kepegawaian.dto.master.jenjangPendidikan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@Data
public class JenjangPendidikanPostRequest {
    @NotEmpty(message = "Nama is required")
    private String nama;
    private String shortName;
    @Min(value = 1, message = "Seq is required")
    private Integer seq;
    private Boolean isStatistik = Boolean.FALSE;

    @JsonIgnore
    public Specification<JenjangPendidikan> getSpecification() {
        return SpecificationBuilder.<JenjangPendidikan>of()
                .addEqual(nama,"nama")
                .build();
    }

}
