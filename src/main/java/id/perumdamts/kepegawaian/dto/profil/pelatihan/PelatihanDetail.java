package id.perumdamts.kepegawaian.dto.profil.pelatihan;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PelatihanDetail {
    private Long id;
    private String biodataId;
    private String biodataNik;
    private String biodataNama;
    private Long jenisPelatihanId;
    private String jenisPelatihanNama;
    private String nama;
    private String lembaga;
    private LocalDate tanggalMulai;
    private LocalDate tanggalSelesai;
    private Boolean lulus;
    private String nilai;
    private Boolean ikatanDinas;
    private LocalDate tanggalAkhirIkatan;
    private String notes;
    private Byte changedStatus;
    private List<LampiranRow> lampiran;
}