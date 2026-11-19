package id.perumdamts.kepegawaian.dto.master.alasanBerhenti;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AlasanBerhentiPostRequest {
    @NotEmpty(message = "Nama is required")
    private String nama;
    private String notes;



}
