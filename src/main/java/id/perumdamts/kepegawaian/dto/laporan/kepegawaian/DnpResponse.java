package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

public record DnpResponse(
        String kodeOrganisasi,
        Integer levelJabatan,
        String nama,
        String nipam,
        String namaJabatan,
        String tmtJabatan,
        String pangkat,
        String golongan,
        String tmtGolongan,
        Integer mkgTahun,
        Integer mkgBulan,
        String tmtKerja,
        Integer mkTahun,
        Integer mkBulan,
        String pendidikan,
        String ttl
) {}
