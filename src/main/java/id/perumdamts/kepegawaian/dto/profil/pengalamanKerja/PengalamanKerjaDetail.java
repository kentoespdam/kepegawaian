package id.perumdamts.kepegawaian.dto.profil.pengalamanKerja;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;
import lombok.Data;

import java.util.List;

@Data
public class PengalamanKerjaDetail {
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
    private List<LampiranRow> lampiran;
}