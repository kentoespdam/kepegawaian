package id.perumdamts.kepegawaian.dto.profil.pelatihan;

import com.fasterxml.jackson.annotation.JsonFormat;
import id.perumdamts.kepegawaian.entities.profil.Pelatihan;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PelatihanResponse(
        Long id,
        String biodataId,
        String biodataNama,
        Long jenisPelatihanId,
        String jenisPelatihanNama,
        String nama,
        String lembaga,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalMulai,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalSelesai,
        Boolean lulus,
        String nilai,
        Boolean ikatanDinas,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalAkhirIkatan,
        String notes,
        Boolean disetujui,
        LocalDateTime tanggalPengajuan,
        LocalDateTime tanggalDisetujui,
        String disetujuiOleh,
        Boolean changedStatus
) {
    public static PelatihanResponse from(Pelatihan entity) {
        return new PelatihanResponse(
                entity.getId(),
                entity.getBiodata().getNik(),
                entity.getBiodata().getNama(),
                entity.getJenisPelatihan().getId(),
                entity.getJenisPelatihan().getNama(),
                entity.getNama(),
                entity.getLembaga(),
                entity.getTanggalMulai(),
                entity.getTanggalSelesai(),
                entity.getLulus(),
                entity.getNilai(),
                entity.getIkatanDinas(),
                entity.getTanggalAkhirIkatan(),
                entity.getNotes(),
                entity.getDisetujui(),
                entity.getTanggalPengajuan(),
                entity.getTanggalDisetujui(),
                entity.getDisetujuiOleh(),
                entity.getChangedStatus()
        );
    }
}
