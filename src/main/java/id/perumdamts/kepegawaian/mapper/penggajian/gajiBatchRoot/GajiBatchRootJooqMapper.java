package id.perumdamts.kepegawaian.mapper.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootResponse;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.GajiBatchRoot.GAJI_BATCH_ROOT;

public final class GajiBatchRootJooqMapper {
    private GajiBatchRootJooqMapper() {}

    public static GajiBatchRootResponse mapToResponse(Record record) {
        if (record == null) return null;
        GajiBatchRootResponse response = new GajiBatchRootResponse();
        response.setId(record.get(GAJI_BATCH_ROOT.ID));
        response.setPeriode(record.get(GAJI_BATCH_ROOT.PERIODE));

        var status = record.get(GAJI_BATCH_ROOT.STATUS);
        if (status != null) {
            response.setStatus(EProsesGaji.values()[status]);
        }

        response.setTotalPegawai(record.get(GAJI_BATCH_ROOT.TOTAL_PEGAWAI));
        response.setTanggalProses(record.get(GAJI_BATCH_ROOT.TANGGAL_PROSES));
        response.setDiProsesOleh(record.get(GAJI_BATCH_ROOT.DI_PROSES_OLEH));
        response.setJabatanPemroses(record.get(GAJI_BATCH_ROOT.JABATAN_PEMROSES));

        response.setTanggalVerifikasiTahap1(record.get(GAJI_BATCH_ROOT.TANGGAL_VERIFIKASI_TAHAP1));
        response.setDiVerifikasiOlehTahap1(record.get(GAJI_BATCH_ROOT.DI_VERIFIKASI_OLEH_TAHAP1));
        response.setJabatanVerifikasiTahap1(record.get(GAJI_BATCH_ROOT.JABATAN_VERIFIKASI_TAHAP1));

        response.setTanggalVerifikasiTahap2(record.get(GAJI_BATCH_ROOT.TANGGAL_VERIFIKASI_TAHAP2));
        response.setDiVerifikasiOlehTahap2(record.get(GAJI_BATCH_ROOT.DI_VERIFIKASI_OLEH_TAHAP2));
        response.setJabatanVerifikasiTahap2(record.get(GAJI_BATCH_ROOT.JABATAN_VERIFIKASI_TAHAP2));

        response.setTanggalPersetujuan(record.get(GAJI_BATCH_ROOT.TANGGAL_PERSETUJUAN));
        response.setDiSetujuiOleh(record.get(GAJI_BATCH_ROOT.DI_SETUJUI_OLEH));
        response.setJabatanPenyetuju(record.get(GAJI_BATCH_ROOT.JABATAN_PENYETUJU));
        response.setNotes(record.get(GAJI_BATCH_ROOT.NOTES));

        return response;
    }
}
