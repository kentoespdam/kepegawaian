package id.perumdamts.kepegawaian.dto.profil.pelatihan;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PelatihanIndexQuery extends PagedRequest {
    @NotBlank(message = "Biodata ID is required")
    private String biodataId;
    private Long jenisPelatihanId;
    private String nama;
    private String lembaga;
}