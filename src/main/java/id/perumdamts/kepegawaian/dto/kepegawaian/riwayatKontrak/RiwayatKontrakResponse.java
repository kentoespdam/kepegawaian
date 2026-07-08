package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.commons.EJenisKontrak;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatKontrak;

import java.time.LocalDate;

public record RiwayatKontrakResponse(
        Long id,
        EJenisKontrak jenisKontrak,
        String nipam,
        String nama,
        String nomorKontrak,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalSk,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalMulai,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalSelesai,
        String notes
) {
    public static RiwayatKontrakResponse from(RiwayatKontrak entity) {
        return new RiwayatKontrakResponse(
                entity.getId(),
                entity.getJenisKontrak(),
                entity.getNipam(),
                entity.getNama(),
                entity.getNomorKontrak(),
                entity.getTanggalSk(),
                entity.getTanggalMulai(),
                entity.getTanggalSelesai(),
                entity.getNotes()
        );
    }
}
