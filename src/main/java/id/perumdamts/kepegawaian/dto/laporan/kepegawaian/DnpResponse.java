package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import java.time.LocalDate;

public record DnpResponse(
        String kodeOrganisasi,
        String orgGroup,
        Integer levelJabatan,
        String nama,
        String nipam,
        String namaJabatan,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "dd.MM.yyyy")
        LocalDate tmtJabatan,
        String pangkat,
        String golongan,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "dd.MM.yyyy")
        LocalDate tmtGolongan,
        Integer mkgTahun,
        Integer mkgBulan,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "dd.MM.yyyy")
        LocalDate tmtKerja,
        Integer mkTahun,
        Integer mkBulan,
        String pendidikan,
        String ttl
) {}
