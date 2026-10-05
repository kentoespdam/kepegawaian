package id.perumdamts.kepegawaian.dto.master.jenisKeahlian;

import id.perumdamts.kepegawaian.entities.master.JenisKeahlian;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class JenisKeahlianPostRequest {
    @NotEmpty(message = "Nama is required")
    private String nama;



}
