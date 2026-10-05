package id.perumdamts.kepegawaian.dto.master.jenisKitas;

import id.perumdamts.kepegawaian.entities.master.JenisKitas;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class JenisKitasPostRequest {
    @NotEmpty(message = "Nama is required")
    private String nama;



}
