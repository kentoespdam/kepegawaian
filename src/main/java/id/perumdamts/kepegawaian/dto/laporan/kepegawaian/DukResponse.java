package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

import java.time.LocalDate;

public record DukResponse(
        String nama,
        String nipam,
        String golongan,
        String pangkat,
        LocalDate tmtGolongan,
        String namaJabatan,
        LocalDate tmtJabatan,
        LocalDate tmtKerja,
        Integer mkTahun,
        Integer mkBulan,
        Integer usia,
        String jurusan,
        Integer tahunLulus,
        String tingkatPendidikan,
        Byte statusPegawai
) {}
