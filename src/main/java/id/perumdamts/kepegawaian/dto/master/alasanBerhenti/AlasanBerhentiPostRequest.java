package id.perumdamts.kepegawaian.dto.master.alasanBerhenti;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.AlasanBerhenti;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class AlasanBerhentiPostRequest {
    @NotEmpty(message = "Nama is required")
    private String nama;
    private String notes;

    @JsonIgnore
    public Specification<AlasanBerhenti> getSpecification() {
        return SpecificationBuilder.<AlasanBerhenti>of()
                .addEqual(nama, "nama")
                .build();
    }

}
