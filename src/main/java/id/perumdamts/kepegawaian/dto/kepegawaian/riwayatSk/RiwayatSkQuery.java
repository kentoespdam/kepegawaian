package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;

import java.time.LocalDate;

public record RiwayatSkQuery(
        Long id,
        String nipam,
        String nama,
        String nomorSk,
        EJenisSk jenisSk,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalSk,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tmtBerlaku,
        GolonganResponse golongan,
        Double gajiPokok,
        Integer mkgTahun,
        Integer mkgBulan,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate kenaikanBerikutnya,
        Integer mkgbTahun,
        Integer mkgbBulan,
        Boolean updateMaster,
        String notes
) {}
