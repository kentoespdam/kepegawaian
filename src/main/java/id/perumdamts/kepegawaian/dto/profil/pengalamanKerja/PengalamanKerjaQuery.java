package id.perumdamts.kepegawaian.dto.profil.pengalamanKerja;

import lombok.Data;

@Data
public class PengalamanKerjaQuery {
    private Long id;
    private String biodataId;
    private String biodataNik;
    private String biodataNama;
    private String namaPerusahaan;
    private String typePerusahaan;
    private String jabatan;
    private String lokasi;
    private Integer tahunMasuk;
    private Integer tahunKeluar;
    private String notes;
    private Byte changedStatus;
}