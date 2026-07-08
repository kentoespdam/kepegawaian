package id.perumdamts.kepegawaian.mapper.kepegawaian;

import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkResponse;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.RiwayatSk.RIWAYAT_SK;

public final class RiwayatSkJooqMapper {

    private RiwayatSkJooqMapper() {}

    public static RiwayatSkResponse mapRiwayatSk(Record r) {
        RiwayatSkResponse res = new RiwayatSkResponse();
        res.setId(r.get(RIWAYAT_SK.ID));
        res.setNipam(r.get(RIWAYAT_SK.NIPAM));
        res.setNama(r.get(RIWAYAT_SK.NAMA));
        res.setNomorSk(r.get(RIWAYAT_SK.NOMOR_SK));

        Byte jsByte = r.get(RIWAYAT_SK.JENIS_SK);
        res.setJenisSk(jsByte != null ? EJenisSk.values()[jsByte] : null);

        res.setTanggalSk(r.get(RIWAYAT_SK.TANGGAL_SK));
        res.setTmtBerlaku(r.get(RIWAYAT_SK.TMT_BERLAKU));

        Long golId = r.get("golongan_id", Long.class);
        if (golId != null) {
            res.setGolongan(new GolonganResponse(
                    golId,
                    r.get("golongan_golongan", String.class),
                    r.get("golongan_pangkat", String.class)
            ));
        }

        res.setGajiPokok(r.get(RIWAYAT_SK.GAJI_POKOK));
        res.setMkgTahun(r.get(RIWAYAT_SK.MKG_TAHUN));
        res.setMkgBulan(r.get(RIWAYAT_SK.MKG_BULAN));
        res.setKenaikanBerikutnya(r.get(RIWAYAT_SK.KENAIKAN_BERIKUTNYA));
        res.setMkgbTahun(r.get(RIWAYAT_SK.MKGB_TAHUN));
        res.setMkgbBulan(r.get(RIWAYAT_SK.MKGB_BULAN));
        res.setUpdateMaster(r.get(RIWAYAT_SK.UPDATE_MASTER));
        res.setNotes(r.get(RIWAYAT_SK.NOTES));
        return res;
    }
}
