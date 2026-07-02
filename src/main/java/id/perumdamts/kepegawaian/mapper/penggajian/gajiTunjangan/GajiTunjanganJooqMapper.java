package id.perumdamts.kepegawaian.mapper.penggajian.gajiTunjangan;

import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.GajiTunjangan.GAJI_TUNJANGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Level.LEVEL;

public final class GajiTunjanganJooqMapper {
    private GajiTunjanganJooqMapper() {}

    public static GajiTunjanganResponse mapToResponse(Record record) {
        if (record == null) return null;
        GajiTunjanganResponse response = new GajiTunjanganResponse();
        response.setId(record.get(GAJI_TUNJANGAN.ID));
        response.setNominal(record.get(GAJI_TUNJANGAN.NOMINAL));

        var jenis = record.get(GAJI_TUNJANGAN.JENIS_TUNJANGAN);
        if (jenis != null) {
            response.setJenisTunjangan(EJenisTunjangan.values()[jenis.intValue()]);
        }

        if (record.get(GAJI_TUNJANGAN.LEVEL_ID) != null) {
            response.setLevel(new LevelResponse(
                    record.get(GAJI_TUNJANGAN.LEVEL_ID),
                    record.get(LEVEL.NAMA)
            ));
        }

        if (record.get(GAJI_TUNJANGAN.GOLONGAN_ID) != null) {
            response.setGolongan(new GolonganResponse(
                    record.get(GAJI_TUNJANGAN.GOLONGAN_ID),
                    record.get(GOLONGAN.GOLONGAN_),
                    record.get(GOLONGAN.PANGKAT)
            ));
        }

        return response;
    }
}
