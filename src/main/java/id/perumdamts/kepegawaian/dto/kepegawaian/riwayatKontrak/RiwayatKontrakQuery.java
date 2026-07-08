package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.commons.EJenisKontrak;

import java.time.LocalDate;

public record RiwayatKontrakQuery(
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
) {}
