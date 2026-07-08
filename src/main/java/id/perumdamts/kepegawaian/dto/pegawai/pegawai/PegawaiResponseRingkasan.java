package id.perumdamts.kepegawaian.dto.pegawai.pegawai;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.time.LocalDate;

public record PegawaiResponseRingkasan(
        Long id,
        String nipam,
        String nama,
        String jenisKelamin,
        String tempatLahir,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tanggalLahir,
        String statusKawin,
        String alamat,
        String nik,
        String agama,
        String telp,
        String email,
        String kodePajak,
        String ibuKandung,
        String pendidikanTerakhir,
        String lembagaPendidikan,
        Integer tahunLulus,
        String statusPegawai,
        String pangkatGolongan,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tmtGolongan,
        String mkg,
        String unitKerja,
        String jabatan,
        String profesi,
        String grade,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tmtKerja,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tmtPegawai,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tmtPensiun,
        Boolean isAskes,
        Integer absensiId,
        String noKontrak,
        String noNpwp,
        String noJamsostek,
        String noBpjs,
        String noIdCard
) {}
