package id.perumdamts.kepegawaian.dto.master.jenisKeahlian;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class JenisKeahlianPostRequest {
    @NotEmpty(message = "Nama is required")
    private String nama;



}
