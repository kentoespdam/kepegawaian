package id.perumdamts.kepegawaian.dto.penggajian.dasarGaji;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

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
}

