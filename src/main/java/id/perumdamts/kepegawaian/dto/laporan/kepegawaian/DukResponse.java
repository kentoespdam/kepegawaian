package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import java.time.LocalDate;

public record DukResponse(
        String nama,
        String nipam,
        String golongan,
        String pangkat,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "dd.MM.yyyy")
        LocalDate tmtGolongan,
        String namaJabatan,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "dd.MM.yyyy")
        LocalDate tmtJabatan,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "dd.MM.yyyy")
        LocalDate tmtKerja,
        Integer mkTahun,
        Integer mkBulan,
        Integer usia,
        String jurusan,
        Integer tahunLulus,
        String tingkatPendidikan,
        String statusPegawai
) {}
