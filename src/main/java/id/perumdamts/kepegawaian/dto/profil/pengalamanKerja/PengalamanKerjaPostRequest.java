package id.perumdamts.kepegawaian.dto.profil.pengalamanKerja;

import id.perumdamts.kepegawaian.entities.profil.PengalamanKerja;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PengalamanKerjaPostRequest {
    @NotEmpty(message = "Biodata ID is required")
    private String biodataId;
    @NotEmpty(message = "Nama Perusahaan is required")
    private String namaPerusahaan;
    private String typePerusahaan;
    private String jabatan;
    private String lokasi;
    private Integer tahunMasuk;
    private Integer tahunKeluar;
    private String notes;


}
