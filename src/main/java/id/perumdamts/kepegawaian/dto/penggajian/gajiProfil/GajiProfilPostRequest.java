package id.perumdamts.kepegawaian.dto.penggajian.gajiProfil;

import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GajiProfilPostRequest {
    @NotEmpty(message = "Nama is required")
    @NotNull(message = "Nama is required")
    private String nama;



}
