package id.perumdamts.kepegawaian.mapper.penggajian.gajiKomponen;

import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.GajiKomponenMiniProjection;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.GajiKomponenResponse;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import lombok.Data;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.GajiKomponen.GAJI_KOMPONEN;
import static id.perumdamts.kepegawaian.jooq.tables.GajiProfil.GAJI_PROFIL;

public final class GajiKomponenJooqMapper {
    private GajiKomponenJooqMapper() {}

    public static GajiKomponenResponse mapToResponse(Record record) {
        if (record == null) return null;
        var jenisGajiObj = record.get(GAJI_KOMPONEN.JENIS_GAJI);
        EJenisGaji jenisGaji = jenisGajiObj != null ? EJenisGaji.valueOf(jenisGajiObj.name()) : null;

        GajiProfilResponse profilGaji = record.get(GAJI_KOMPONEN.PROFIL_GAJI_ID) != null
                ? new GajiProfilResponse(
                record.get(GAJI_KOMPONEN.PROFIL_GAJI_ID),
                record.get(GAJI_PROFIL.NAMA))
                : null;

        return new GajiKomponenResponse(
                record.get(GAJI_KOMPONEN.ID),
                record.get(GAJI_KOMPONEN.URUT),
                profilGaji,
                record.get(GAJI_KOMPONEN.KODE),
                record.get(GAJI_KOMPONEN.NAMA),
                jenisGaji,
                record.get(GAJI_KOMPONEN.NILAI),
                record.get(GAJI_KOMPONEN.IS_REFERENCE),
                record.get(GAJI_KOMPONEN.FORMULA)
        );
    }

    public static GajiKomponenMiniProjection mapToMiniProjection(Record record) {
        if (record == null) return null;
        GajiKomponenMiniDto dto = new GajiKomponenMiniDto();
        dto.setKode(record.get(GAJI_KOMPONEN.KODE));
        dto.setNama(record.get(GAJI_KOMPONEN.NAMA));
        return dto;
    }

    @Data
    private static class GajiKomponenMiniDto implements GajiKomponenMiniProjection {
        private String kode;
        private String nama;
    }
}
