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
        var statusByte = record.get(GAJI_POTONGAN_TKK.STATUS_PEGAWAI);
        EStatusPegawai statusPegawai = statusByte != null ? EStatusPegawai.values()[statusByte.intValue()] : null;

        LevelResponse level = record.get(GAJI_POTONGAN_TKK.LEVEL_ID) != null
                ? new LevelResponse(record.get(GAJI_POTONGAN_TKK.LEVEL_ID), record.get(LEVEL.NAMA))
                : null;

        GolonganResponse golongan = record.get(GAJI_POTONGAN_TKK.GOLONGAN_ID) != null
                ? new GolonganResponse(
                record.get(GAJI_POTONGAN_TKK.GOLONGAN_ID),
                record.get(GOLONGAN.GOLONGAN_),
                record.get(GOLONGAN.PANGKAT))
                : null;

        return new GajiPotonganTkkResponse(
                record.get(GAJI_POTONGAN_TKK.ID),
                statusPegawai,
                level,
                golongan,
                record.get(GAJI_POTONGAN_TKK.NOMINAL)
        );
    }
}
