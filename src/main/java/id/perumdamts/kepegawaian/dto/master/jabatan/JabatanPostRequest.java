package id.perumdamts.kepegawaian.dto.master.jabatan;

import id.perumdamts.kepegawaian.entities.master.Jabatan;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JabatanPostRequest {
    @NotEmpty(message = "Kode is required")
    @NotNull(message = "Kode is required")
    private String kode;
    @Min(value = 1, message = "Jabatan Induk ID must be greater than or equal to 1")
    private Long parentId;
    @Min(value = 1, message = "Organisasi ID must be greater than or equal to 1")
    private Long organisasiId;
    @Min(value = 1, message = "Level ID must be greater than or equal to 1")
    private Long levelId;
    @NotEmpty(message = "Nama is required")
    private String nama;



}
