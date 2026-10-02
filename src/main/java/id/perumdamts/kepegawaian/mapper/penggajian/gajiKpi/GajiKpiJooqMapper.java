package id.perumdamts.kepegawaian.mapper.penggajian.gajiKpi;

import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiResponse;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.GajiKpi.GAJI_KPI;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;

public final class GajiKpiJooqMapper {
    private GajiKpiJooqMapper() {}

    public static GajiKpiResponse mapToResponse(Record record) {
        if (record == null) return null;
        Byte spByte = record.get(PEGAWAI.STATUS_PEGAWAI);
        String statusPegawai = spByte != null ? EStatusPegawai.values()[spByte].value : null;
        return new GajiKpiResponse(
                record.get(GAJI_KPI.ID),
                record.get(GAJI_KPI.NIPAM),
                record.get(GAJI_KPI.PERIODE),
                record.get(GAJI_KPI.TUNKIN),
                record.get(GAJI_KPI.PPH21_TER),
                record.get(BIODATA.NAMA),
                record.get(JABATAN.NAMA),
                record.get(ORGANISASI.NAMA),
                statusPegawai
        );
    }
}
