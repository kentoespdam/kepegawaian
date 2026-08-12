package id.perumdamts.kepegawaian.dto.profil.kartuIdentitas;

import com.fasterxml.jackson.annotation.JsonFormat;
import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;

import java.time.LocalDate;

public record KartuIdentitasResponse(
        Long id,
        String biodataId,
        String biodataNama,
        Long jenisKartuId,
        String jenisKartuNama,
        String nomorKartu,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalExpired,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalTerima,
        String notes,
        Boolean changedStatus
) {
    public static KartuIdentitasResponse from(KartuIdentitas entity) {
        return new KartuIdentitasResponse(
                entity.getId(),
                entity.getBiodata().getNik(),
                entity.getBiodata().getNama(),
                entity.getJenisKartu().getId(),
                entity.getJenisKartu().getNama(),
                entity.getNomorKartu(),
                entity.getTanggalExpired(),
                entity.getTanggalTerima(),
                entity.getNotes(),
                entity.getChangedStatus()
        );
    }
}
