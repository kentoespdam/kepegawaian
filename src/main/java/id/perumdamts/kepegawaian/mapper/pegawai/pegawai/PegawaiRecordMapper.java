package id.perumdamts.kepegawaian.mapper.pegawai.pegawai;

import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkResponse;
import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiListResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;
import static id.perumdamts.kepegawaian.jooq.tables.RiwayatSk.RIWAYAT_SK;

public final class PegawaiRecordMapper {

    private PegawaiRecordMapper() {
    }

    public static PegawaiResponse mapResponse(Record record) {
        Byte statusPegawaiByte = record.get(PEGAWAI.STATUS_PEGAWAI);
        EStatusPegawai statusPegawai = statusPegawaiByte != null ? EStatusPegawai.values()[statusPegawaiByte] : null;

        Byte statusKerjaByte = record.get(PEGAWAI.STATUS_KERJA);
        EStatusKerja statusKerja = statusKerjaByte != null ? EStatusKerja.values()[statusKerjaByte] : null;

        String bioNik = record.get("biodata_nik", String.class);
        PegawaiResponse.Biodata biodata = bioNik != null
                ? new PegawaiResponse.Biodata(
                bioNik,
                record.get("biodata_nama", String.class),
                record.get("biodata_gelar_depan", String.class),
                record.get("biodata_gelar_belakang", String.class)
        ) : null;

        Long orgId = record.get("organisasi_id", Long.class);
        PegawaiResponse.Organisasi organisasi = orgId != null
                ? new PegawaiResponse.Organisasi(orgId, record.get("organisasi_nama", String.class))
                : null;

        Long jabId = record.get("jabatan_id", Long.class);
        PegawaiResponse.Jabatan jabatan = jabId != null
                ? new PegawaiResponse.Jabatan(jabId, record.get("jabatan_nama", String.class))
                : null;

        Long profId = record.get("profesi_id", Long.class);
        PegawaiResponse.Profesi profesi = profId != null
                ? new PegawaiResponse.Profesi(profId, record.get("profesi_nama", String.class))
                : null;

        Long golId = record.get("golongan_id", Long.class);
        PegawaiResponse.Golongan golongan = golId != null
                ? new PegawaiResponse.Golongan(
                golId,
                record.get("golongan_golongan", String.class),
                record.get("golongan_pangkat", String.class)
        ) : null;

        Long grdId = record.get("grade_id", Long.class);
        PegawaiResponse.Grade grade = grdId != null
                ? new PegawaiResponse.Grade(grdId, record.get("grade_grade", Integer.class))
                : null;

        Long pajakId = record.get("kode_pajak_id", Long.class);
        String pajakKode = pajakId != null ? record.get("kode_pajak_kode", String.class) : null;
        PegawaiResponse.KodePajak kodePajak = pajakId != null
                ? new PegawaiResponse.KodePajak(pajakId, pajakKode, pajakKode)
                : null;

        return new PegawaiResponse(
                record.get(PEGAWAI.ID),
                record.get(PEGAWAI.NIPAM),
                biodata,
                statusPegawai,
                organisasi,
                jabatan,
                profesi,
                golongan,
                grade,
                statusKerja,
                record.get(PEGAWAI.REF_SK_CAPEG_ID),
                record.get(PEGAWAI.TMT_KERJA),
                record.get(PEGAWAI.TMT_PENSIUN),
                record.get(PEGAWAI.REF_SK_PEGAWAI_ID),
                record.get(PEGAWAI.TMT_PEGAWAI),
                record.get(PEGAWAI.REF_SK_GOL_ID),
                record.get(PEGAWAI.TMT_GOLONGAN),
                record.get(PEGAWAI.REF_SK_JABATAN_ID),
                record.get(PEGAWAI.TMT_JABATAN),
                record.get(PEGAWAI.REF_SK_MUTASI_ID),
                record.get(PEGAWAI.TMT_MUTASI),
                record.get(PEGAWAI.GAJI_POKOK),
                record.get(PEGAWAI.PHDP),
                record.get(PEGAWAI.JML_TANGGUNGAN),
                kodePajak,
                record.get(PEGAWAI.IS_ASKES),
                record.get(PEGAWAI.MKG_TAHUN),
                record.get(PEGAWAI.MKG_BULAN),
                record.get(PEGAWAI.EMAIL),
                record.get(PEGAWAI.ABSENSI_ID),
                record.get(PEGAWAI.NOTES)
        );
    }

    public static PegawaiListResponse mapListResponse(Record record) {
        Byte statusPegawaiByte = record.get(PEGAWAI.STATUS_PEGAWAI);
        EStatusPegawai statusPegawai = statusPegawaiByte != null ? EStatusPegawai.values()[statusPegawaiByte] : null;

        Long orgId = record.get("organisasi_id", Long.class);
        OrganisasiMiniResponse organisasi = orgId != null
                ? new OrganisasiMiniResponse(
                orgId,
                record.get("organisasi_kode", String.class),
                record.get("organisasi_nama", String.class),
                record.get("organisasi_short_name", String.class))
                : null;

        Long jabId = record.get("jabatan_id", Long.class);
        JabatanMiniResponse jabatan = null;
        if (jabId != null) {
            Long lvlId = record.get("level_id", Long.class);
            jabatan = new JabatanMiniResponse(
                    jabId,
                    record.get("jabatan_kode", String.class),
                    lvlId != null ? new LevelResponse(lvlId, record.get("level_nama", String.class)) : null,
                    record.get("jabatan_nama", String.class));
        }

        Long golId = record.get("golongan_id", Long.class);
        GolonganResponse golongan = golId != null
                ? new GolonganResponse(
                golId,
                record.get("golongan_golongan", String.class),
                record.get("golongan_pangkat", String.class)
        ) : null;

        return new PegawaiListResponse(
                record.get(PEGAWAI.ID),
                record.get(PEGAWAI.NIPAM),
                record.get("biodata_nama", String.class),
                statusPegawai,
                organisasi,
                jabatan,
                golongan
        );
    }

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
