package id.perumdamts.kepegawaian.dto.pegawai.pegawai;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;

import java.time.LocalDate;

public record PegawaiResponse(
        Long id,
        String nipam,
        Biodata biodata,
        EStatusPegawai statusPegawai,
        Organisasi organisasi,
        Jabatan jabatan,
        Profesi profesi,
        Golongan golongan,
        Grade grade,
        EStatusKerja statusKerja,
        Long refSkCapegId,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tmtKerja,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tmtPensiun,
        Long refSkPegawaiId,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tmtPegawai,
        Long refSkGolId,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tmtGolongan,
        Long refSkJabatanId,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tmtJabatan,
        Long refSkMutasiId,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tmtMutasi,
        Double gajiPokok,
        Double phdp,
        Integer jmlTanggungan,
        KodePajak kodePajak,
        Boolean isAskes,
        Integer mkgTahun,
        Integer mkgBulan,
        String email,
        Long absensiId,
        String notes
) {

    public record Biodata(
            String nik,
            String nama,
            String gelarDepan,
            String gelarBelakang
    ) {}

    public record Organisasi(
            Long id,
            String nama
    ) {}

    public record Jabatan(
            Long id,
            String nama
    ) {}

    public record Profesi(
            Long id,
            String nama
    ) {}

    public record Golongan(
            Long id,
            String golongan,
            String pangkat
    ) {}

    public record Grade(
            Long id,
            Integer grade
    ) {}

    public record KodePajak(
            Long id,
            String nama,
            String kode
    ) {}
}
