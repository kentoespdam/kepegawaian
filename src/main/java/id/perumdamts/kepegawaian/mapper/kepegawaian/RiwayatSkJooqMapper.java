package id.perumdamts.kepegawaian.mapper.kepegawaian;

import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkResponse;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.RiwayatSk.RIWAYAT_SK;

public final class RiwayatSkJooqMapper {

    private RiwayatSkJooqMapper() {}

    public static RiwayatSkResponse mapRiwayatSk(Record r) {
        Byte jsByte = r.get(RIWAYAT_SK.JENIS_SK);
        EJenisSk jenisSk = jsByte != null ? EJenisSk.values()[jsByte] : null;

        Long golId = r.get("golongan_id", Long.class);
        GolonganResponse golongan = golId != null ? new GolonganResponse(
                golId,
                r.get("golongan_golongan", String.class),
                r.get("golongan_pangkat", String.class)
        ) : null;

        return new RiwayatSkResponse(
                r.get(RIWAYAT_SK.ID),
                r.get(RIWAYAT_SK.NIPAM),
                r.get(RIWAYAT_SK.NAMA),
                r.get(RIWAYAT_SK.NOMOR_SK),
                jenisSk,
                r.get(RIWAYAT_SK.TANGGAL_SK),
                r.get(RIWAYAT_SK.TMT_BERLAKU),
                golongan,
                r.get(RIWAYAT_SK.GAJI_POKOK),
                r.get(RIWAYAT_SK.MKG_TAHUN),
                r.get(RIWAYAT_SK.MKG_BULAN),
                r.get(RIWAYAT_SK.KENAIKAN_BERIKUTNYA),
                r.get(RIWAYAT_SK.MKGB_TAHUN),
                r.get(RIWAYAT_SK.MKGB_BULAN),
                r.get(RIWAYAT_SK.UPDATE_MASTER),
                r.get(RIWAYAT_SK.NOTES)
        );
    }
}
