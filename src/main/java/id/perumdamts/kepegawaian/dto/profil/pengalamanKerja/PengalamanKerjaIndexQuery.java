package id.perumdamts.kepegawaian.dto.profil.pengalamanKerja;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PengalamanKerjaIndexQuery extends PagedRequest {
    @NotBlank(message = "Biodata ID is required")
    private String biodataId;
    private String namaPerusahaan;
    private String jabatan;
}