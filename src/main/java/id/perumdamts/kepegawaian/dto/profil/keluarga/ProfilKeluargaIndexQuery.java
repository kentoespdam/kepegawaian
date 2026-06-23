package id.perumdamts.kepegawaian.dto.profil.keluarga;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProfilKeluargaIndexQuery extends PagedRequest {
    @NotBlank(message = "Biodata ID is required")
    private String biodataId;

    private Integer hubunganKeluarga;
    private Integer jenisKelamin;
    private Boolean isDeleted = false;
}