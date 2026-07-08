package id.perumdamts.kepegawaian.dto.penggajian.dasarGaji;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.penggajian.DasarGaji;

import java.time.LocalDate;

public record DasarGajiResponse(
        Long id,
        String deskripsi,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalMulai,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalAkhir,
        Boolean aktif
) {
    public static DasarGajiResponse from(DasarGaji entity) {
        return new DasarGajiResponse(
                entity.getId(),
                entity.getDeskripsi(),
                entity.getTanggalAwal(),
                entity.getTanggalAkhir(),
                entity.isAktif()
        );
    }
}

