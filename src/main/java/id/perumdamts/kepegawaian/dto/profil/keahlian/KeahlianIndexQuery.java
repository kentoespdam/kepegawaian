package id.perumdamts.kepegawaian.dto.profil.keahlian;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class KeahlianIndexQuery extends PagedRequest {
    @NotBlank
    private String biodataId;
    private Long jenisKeahlianId;
    private Boolean disetujui;
}
