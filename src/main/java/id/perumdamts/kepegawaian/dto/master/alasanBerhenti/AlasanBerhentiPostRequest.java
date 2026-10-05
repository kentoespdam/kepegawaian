package id.perumdamts.kepegawaian.dto.master.alasanBerhenti;

import id.perumdamts.kepegawaian.entities.master.AlasanBerhenti;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AlasanBerhentiPostRequest {
    @NotEmpty(message = "Nama is required")
    private String nama;
    private String notes;



}
