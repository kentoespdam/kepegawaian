package id.perumdamts.kepegawaian.dto.master.organisasi;

import id.perumdamts.kepegawaian.entities.master.Organisasi;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrganisasiPostRequest {
    @NotBlank(message = "Kode organisasi tidak boleh kosong")
    @Size(max = 64, message = "Kode organisasi maksimal 64 karakter")
    private String kode;
    private Long parentId;
    private Integer levelOrganisasi;
    @NotEmpty(message = "Nama tidak boleh kosong")
    private String nama;
    private String shortName;
    private String category;
    @NotBlank(message = "Group tidak boleh kosong")
    private String group;


}
