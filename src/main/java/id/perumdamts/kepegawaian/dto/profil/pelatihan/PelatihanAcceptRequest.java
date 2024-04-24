package id.perumdamts.kepegawaian.dto.profil.pelatihan;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PelatihanAcceptRequest {
    @NotEmpty(message = "Biodata ID is required")
    private String biodataId;
}
