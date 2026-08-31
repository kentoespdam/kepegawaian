package id.perumdamts.kepegawaian.dto.laporan.kepegawaian;

public record StatistikPendidikan2Response(
        Long id,
        String pendidikan,
        Integer nonGolongan,
        Integer golonganA,
        Integer golonganB,
        Integer golonganC,
        Integer golonganD,
        Integer jmlGolongan,
        Integer kontrak,
        Integer capeg,
        Integer honorer,
        Integer tetap,
        Integer jmlStatusPegawai,
        Integer adm,
        Integer pelayanan,
        Integer teknik,
        Integer jmlUnitKerja,
        Integer pria,
        Integer wanita,
        Integer jmlJenisKelamin
) {}
