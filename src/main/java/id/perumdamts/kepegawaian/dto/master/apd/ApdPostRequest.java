package id.perumdamts.kepegawaian.dto.master.apd;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.Apd;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class ApdPostRequest {
    @NotNull(message = "Profesi is required")
    private Long profesiId;

    @NotEmpty(message = "Nama is required")
    private String nama;

    @JsonIgnore
    public Specification<Apd> getSpecification() {
        return SpecificationBuilder.<Apd>of()
                .addEqual(nama, "nama")
                .build();
    }
}
