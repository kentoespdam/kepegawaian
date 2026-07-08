package id.perumdamts.kepegawaian.dto.master.jenisKeahlian;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.JenisKeahlian;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class JenisKeahlianPostRequest {
    @NotEmpty(message = "Nama is required")
    private String nama;

    @JsonIgnore
    public Specification<JenisKeahlian> getSpecification() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(criteriaBuilder.equal(root.get("nama"), nama));
    }

}
