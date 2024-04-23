package id.perumdamts.kepegawaian.dto.profil.pendidikan;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PendidikanAcceptRequest {
    @NotEmpty(message = "Biodata ID is required")
    private String biodataId;
    private Boolean isLatest;
}
