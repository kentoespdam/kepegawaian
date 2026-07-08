package id.perumdamts.kepegawaian.dto.profil.kartuIdentitas;

import java.time.LocalDate;

public record KartuIdentitasQuery(
        Long id,
        String biodataId,
        String biodataNik,
        String biodataNama,
        Long jenisKartuId,
        String jenisKartuNama,
        String nomorKartu,
        LocalDate tanggalExpired,
        LocalDate tanggalTerima,
        String notes,
        Byte changedStatus
) {}
