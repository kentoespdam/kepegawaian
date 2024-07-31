package id.perumdamts.kepegawaian.dto.master.jenisMutasi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.JenisMutasi;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class JenisMutasiPostRequest {
    @NotEmpty(message = "Nama is required")
    private String nama;
    private String notes;

    @JsonIgnore
    public Specification<JenisMutasi> getSpecification() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("nama"), this.getNama());
    }

    public static JenisMutasi toEntity(JenisMutasiPostRequest request) {
        JenisMutasi entity = new JenisMutasi();
        entity.setNama(request.getNama());
        entity.setNotes(request.getNotes());
        return entity;
    }
}
