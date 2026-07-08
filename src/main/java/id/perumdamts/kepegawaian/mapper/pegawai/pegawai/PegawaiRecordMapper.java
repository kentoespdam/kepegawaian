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
        PegawaiResponse response = new PegawaiResponse();
        response.setId(record.get(PEGAWAI.ID));
        response.setNipam(record.get(PEGAWAI.NIPAM));

        Byte statusPegawaiByte = record.get(PEGAWAI.STATUS_PEGAWAI);
        response.setStatusPegawai(statusPegawaiByte != null ? EStatusPegawai.values()[statusPegawaiByte] : null);

        Byte statusKerjaByte = record.get(PEGAWAI.STATUS_KERJA);
        response.setStatusKerja(statusKerjaByte != null ? EStatusKerja.values()[statusKerjaByte] : null);

        response.setRefSkCapegId(record.get(PEGAWAI.REF_SK_CAPEG_ID));
        response.setTmtKerja(record.get(PEGAWAI.TMT_KERJA));
        response.setTmtPensiun(record.get(PEGAWAI.TMT_PENSIUN));
        response.setRefSkPegawaiId(record.get(PEGAWAI.REF_SK_PEGAWAI_ID));
        response.setTmtPegawai(record.get(PEGAWAI.TMT_PEGAWAI));
        response.setRefSkGolId(record.get(PEGAWAI.REF_SK_GOL_ID));
        response.setTmtGolongan(record.get(PEGAWAI.TMT_GOLONGAN));
        response.setRefSkJabatanId(record.get(PEGAWAI.REF_SK_JABATAN_ID));
        response.setTmtJabatan(record.get(PEGAWAI.TMT_JABATAN));
        response.setRefSkMutasiId(record.get(PEGAWAI.REF_SK_MUTASI_ID));
        response.setTmtMutasi(record.get(PEGAWAI.TMT_MUTASI));
        response.setGajiPokok(record.get(PEGAWAI.GAJI_POKOK));
        response.setPhdp(record.get(PEGAWAI.PHDP));
        response.setJmlTanggungan(record.get(PEGAWAI.JML_TANGGUNGAN));
        response.setIsAskes(record.get(PEGAWAI.IS_ASKES));
        response.setMkgTahun(record.get(PEGAWAI.MKG_TAHUN));
        response.setMkgBulan(record.get(PEGAWAI.MKG_BULAN));
        response.setEmail(record.get(PEGAWAI.EMAIL));
        response.setAbsensiId(record.get(PEGAWAI.ABSENSI_ID));
        response.setNotes(record.get(PEGAWAI.NOTES));

        String bioNik = record.get("biodata_nik", String.class);
        if (bioNik != null) {
            response.setBiodata(new PegawaiResponse.Biodata(
                    bioNik,
                    record.get("biodata_nama", String.class),
                    record.get("biodata_gelar_depan", String.class),
                    record.get("biodata_gelar_belakang", String.class)
            ));
        }

        Long orgId = record.get("organisasi_id", Long.class);
        if (orgId != null) {
            response.setOrganisasi(new PegawaiResponse.Organisasi(
                    orgId,
                    record.get("organisasi_nama", String.class)
            ));
        }

        Long jabId = record.get("jabatan_id", Long.class);
        if (jabId != null) {
            response.setJabatan(new PegawaiResponse.Jabatan(
                    jabId,
                    record.get("jabatan_nama", String.class)
            ));
        }

        Long profId = record.get("profesi_id", Long.class);
        if (profId != null) {
            response.setProfesi(new PegawaiResponse.Profesi(
                    profId,
                    record.get("profesi_nama", String.class)
            ));
        }

        Long golId = record.get("golongan_id", Long.class);
        if (golId != null) {
            response.setGolongan(new PegawaiResponse.Golongan(
                    golId,
                    record.get("golongan_golongan", String.class),
                    record.get("golongan_pangkat", String.class)
            ));
        }

        Long grdId = record.get("grade_id", Long.class);
        if (grdId != null) {
            response.setGrade(new PegawaiResponse.Grade(
                    grdId,
                    record.get("grade_grade", Integer.class)
            ));
        }

        Long pajakId = record.get("kode_pajak_id", Long.class);
        if (pajakId != null) {
            String pajakKode = record.get("kode_pajak_kode", String.class);
            response.setKodePajak(new PegawaiResponse.KodePajak(
                    pajakId,
                    pajakKode,
                    pajakKode
            ));
        }

        return response;
    }

    public static PegawaiListResponse mapListResponse(Record record) {
        PegawaiListResponse response = new PegawaiListResponse();
        response.setId(record.get(PEGAWAI.ID));
        response.setNipam(record.get(PEGAWAI.NIPAM));
        response.setNama(record.get("biodata_nama", String.class));

        Byte statusPegawaiByte = record.get(PEGAWAI.STATUS_PEGAWAI);
        response.setStatusPegawai(statusPegawaiByte != null ? EStatusPegawai.values()[statusPegawaiByte] : null);

        Long orgId = record.get("organisasi_id", Long.class);
        if (orgId != null) {
            response.setOrganisasi(new OrganisasiMiniResponse(
                    orgId,
                    record.get("organisasi_kode", String.class),
                    record.get("organisasi_nama", String.class),
                    record.get("organisasi_short_name", String.class)));
        }

        Long jabId = record.get("jabatan_id", Long.class);
        if (jabId != null) {
            Long lvlId = record.get("level_id", Long.class);
            response.setJabatan(new JabatanMiniResponse(
                    jabId,
                    record.get("jabatan_kode", String.class),
                    lvlId != null ? new LevelResponse(lvlId, record.get("level_nama", String.class)) : null,
                    record.get("jabatan_nama", String.class)));
        }

        Long golId = record.get("golongan_id", Long.class);
        if (golId != null) {
            response.setGolongan(new GolonganResponse(
                    golId,
                    record.get("golongan_golongan", String.class),
                    record.get("golongan_pangkat", String.class)
            ));
        }

        return response;
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
