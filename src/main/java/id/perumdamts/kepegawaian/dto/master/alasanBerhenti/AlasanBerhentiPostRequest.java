package id.perumdamts.kepegawaian.dto.master.alasanBerhenti;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.AlasanBerhenti;
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
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("nama"), this.getNama());
    }

    public static AlasanBerhenti toEntity(AlasanBerhentiPostRequest request) {
        AlasanBerhenti alasanBerhenti = new AlasanBerhenti();
        alasanBerhenti.setNama(request.getNama());
        alasanBerhenti.setNotes(request.getNotes());
        return alasanBerhenti;
    }
}
