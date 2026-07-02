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
        GajiKomponenResponse response = new GajiKomponenResponse();
        response.setId(record.get(GAJI_KOMPONEN.ID));
        response.setUrut(record.get(GAJI_KOMPONEN.URUT));
        response.setKode(record.get(GAJI_KOMPONEN.KODE));
        response.setNama(record.get(GAJI_KOMPONEN.NAMA));
        response.setNilai(record.get(GAJI_KOMPONEN.NILAI));
        response.setIsReference(record.get(GAJI_KOMPONEN.IS_REFERENCE));
        response.setFormula(record.get(GAJI_KOMPONEN.FORMULA));

        var jenisGaji = record.get(GAJI_KOMPONEN.JENIS_GAJI);
        if (jenisGaji != null) {
            response.setJenisGaji(EJenisGaji.valueOf(jenisGaji.name()));
        }

        if (record.get(GAJI_KOMPONEN.PROFIL_GAJI_ID) != null) {
            GajiProfilResponse profil = new GajiProfilResponse();
            profil.setId(record.get(GAJI_KOMPONEN.PROFIL_GAJI_ID));
            profil.setNama(record.get(GAJI_PROFIL.NAMA));
            response.setProfilGaji(profil);
        }
        return response;
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
