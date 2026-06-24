package id.perumdamts.kepegawaian.dto.profil.kartuIdentitas;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class KartuIdentitasIndexQuery extends PagedRequest {
    @NotBlank(message = "Biodata ID is required")
    private String biodataId;
    private Long jenisKartuId;
    private String nomorKartu;
}
