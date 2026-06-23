package id.perumdamts.kepegawaian.dto.profil.kartuIdentitas;

import lombok.Data;

import java.time.LocalDate;

@Data
public class KartuIdentitasQuery {
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
}
