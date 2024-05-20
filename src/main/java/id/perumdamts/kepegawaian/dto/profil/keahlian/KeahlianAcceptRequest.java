package id.perumdamts.kepegawaian.dto.profil.keahlian;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class KeahlianAcceptRequest {
    @NotEmpty(message = "Biodata ID is required")
    private String biodataId;
}
