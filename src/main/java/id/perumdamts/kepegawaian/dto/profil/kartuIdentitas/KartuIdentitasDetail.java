package id.perumdamts.kepegawaian.dto.profil.kartuIdentitas;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranRow;

import java.time.LocalDate;
import java.util.List;

public record KartuIdentitasDetail(
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
        Byte changedStatus,
        List<LampiranRow> lampiran
) {}
