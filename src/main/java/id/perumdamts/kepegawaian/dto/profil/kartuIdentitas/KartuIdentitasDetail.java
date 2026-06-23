package id.perumdamts.kepegawaian.dto.profil.kartuIdentitas;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class KartuIdentitasDetail {
    private Long id;
    private String biodataId;
    private String biodataNik;
    private String biodataNama;
    private Long jenisKartuId;
    private String jenisKartuNama;
    private String nomorKartu;
    private LocalDate tanggalExpired;
    private LocalDate tanggalTerima;
    private String notes;
    private Byte changedStatus;
    private List<LampiranRow> lampiran;
}
