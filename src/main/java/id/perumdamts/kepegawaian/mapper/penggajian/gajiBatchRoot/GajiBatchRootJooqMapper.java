package id.perumdamts.kepegawaian.mapper.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootResponse;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.GajiBatchRoot.GAJI_BATCH_ROOT;

public final class GajiBatchRootJooqMapper {
    private GajiBatchRootJooqMapper() {}

    public static GajiBatchRootResponse mapToResponse(Record record) {
        if (record == null) return null;

        return new GajiBatchRootResponse(
                record.get(GAJI_BATCH_ROOT.ID),
                record.get(GAJI_BATCH_ROOT.PERIODE),
                mapStatus(record),
                record.get(GAJI_BATCH_ROOT.TOTAL_PEGAWAI),
                record.get(GAJI_BATCH_ROOT.TANGGAL_PROSES),
                record.get(GAJI_BATCH_ROOT.DI_PROSES_OLEH),
                record.get(GAJI_BATCH_ROOT.JABATAN_PEMROSES),
                record.get(GAJI_BATCH_ROOT.TANGGAL_VERIFIKASI_TAHAP1),
                record.get(GAJI_BATCH_ROOT.DI_VERIFIKASI_OLEH_TAHAP1),
                record.get(GAJI_BATCH_ROOT.JABATAN_VERIFIKASI_TAHAP1),
                record.get(GAJI_BATCH_ROOT.TANGGAL_VERIFIKASI_TAHAP2),
                record.get(GAJI_BATCH_ROOT.DI_VERIFIKASI_OLEH_TAHAP2),
                record.get(GAJI_BATCH_ROOT.JABATAN_VERIFIKASI_TAHAP2),
                record.get(GAJI_BATCH_ROOT.TANGGAL_PERSETUJUAN),
                record.get(GAJI_BATCH_ROOT.DI_SETUJUI_OLEH),
                record.get(GAJI_BATCH_ROOT.JABATAN_PENYETUJU),
                record.get(GAJI_BATCH_ROOT.NOTES),
                null,
                null
        );
    }

    private static EProsesGaji mapStatus(Record record) {
        var status = record.get(GAJI_BATCH_ROOT.STATUS);
        if (status != null) {
            return EProsesGaji.values()[status];
        }
        return null;
    }
}
