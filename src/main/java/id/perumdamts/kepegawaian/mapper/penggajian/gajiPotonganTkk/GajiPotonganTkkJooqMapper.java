package id.perumdamts.kepegawaian.mapper.penggajian.gajiPotonganTkk;

import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkResponse;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.GajiPotonganTkk.GAJI_POTONGAN_TKK;
import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Level.LEVEL;

public final class GajiPotonganTkkJooqMapper {
    private GajiPotonganTkkJooqMapper() {}

    public static GajiPotonganTkkResponse mapToResponse(Record record) {
        if (record == null) return null;
        GajiPotonganTkkResponse response = new GajiPotonganTkkResponse();
        response.setId(record.get(GAJI_POTONGAN_TKK.ID));
        response.setNominal(record.get(GAJI_POTONGAN_TKK.NOMINAL));

        var statusByte = record.get(GAJI_POTONGAN_TKK.STATUS_PEGAWAI);
        if (statusByte != null) {
            response.setStatusPegawai(EStatusPegawai.values()[statusByte.intValue()]);
        }

        if (record.get(GAJI_POTONGAN_TKK.LEVEL_ID) != null) {
            response.setLevel(new LevelResponse(
                    record.get(GAJI_POTONGAN_TKK.LEVEL_ID),
                    record.get(LEVEL.NAMA)
            ));
        }

        if (record.get(GAJI_POTONGAN_TKK.GOLONGAN_ID) != null) {
            response.setGolongan(new GolonganResponse(
                    record.get(GAJI_POTONGAN_TKK.GOLONGAN_ID),
                    record.get(GOLONGAN.GOLONGAN_),
                    record.get(GOLONGAN.PANGKAT)
            ));
        }

        return response;
    }
}
