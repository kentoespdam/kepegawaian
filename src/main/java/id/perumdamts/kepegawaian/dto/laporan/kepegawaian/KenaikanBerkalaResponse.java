package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

import java.time.LocalDate;

public record KenaikanBerkalaResponse(
        Long id,
        Long pegawaiId,
        String nipam,
        String nama,
        Byte jenisSk,
        String nomorSk,
        LocalDate tmtBerlaku,
        LocalDate kenaikanBerikutnya,
        LocalDate tanggalEksekusiSanksi,
        Boolean isPendingGaji,
        Boolean isPendingPangkat,
        String namaJabatan,
        LocalDate tmtJabatan,
        String golongan,
        String pangkat,
        LocalDate tmtGolongan,
        Integer mkgTahun,
        Integer mkgBulan,
        LocalDate tmtKerja,
        Integer mkTahun,
        Integer mkBulan,
        String pendidikanTerakhir,
        String tempatLahir,
        LocalDate tanggalLahir
) {}
