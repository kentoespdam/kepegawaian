package id.perumdamts.kepegawaian.repositories.pegawai.jooq;

import org.jooq.Field;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.GajiPendapatanNonPajak.GAJI_PENDAPATAN_NON_PAJAK;
import static id.perumdamts.kepegawaian.jooq.tables.GajiProfil.GAJI_PROFIL;
import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Grade.GRADE;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.JenisKitas.JENIS_KITAS;
import static id.perumdamts.kepegawaian.jooq.tables.JenjangPendidikan.JENJANG_PENDIDIKAN;
import static id.perumdamts.kepegawaian.jooq.tables.KartuIdentitas.KARTU_IDENTITAS;
import static id.perumdamts.kepegawaian.jooq.tables.Level.LEVEL;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;
import static id.perumdamts.kepegawaian.jooq.tables.Profesi.PROFESI;
import static id.perumdamts.kepegawaian.jooq.tables.RumahDinas.RUMAH_DINAS;
import static org.jooq.impl.DSL.select;

public final class PegawaiDetailSelects {
    private PegawaiDetailSelects() {}

    /**
     * Standalone detail fields for PegawaiResponseDetail.
     * Subset berbeda dari {@code pegawaiResponseFields} — includes biodata detail fields,
     * grade_level_nama subquery, gaji_profil, rumah_dinas.
     * String-alias kontrak WAJIB dijaga untuk kompatibilitas mapper.
     */
    public static Field<?>[] detailFields() {
        return new Field<?>[]{
                PEGAWAI.ID,
                PEGAWAI.NIPAM,
                PEGAWAI.STATUS_PEGAWAI,
                PEGAWAI.STATUS_KERJA,
                PEGAWAI.TMT_KERJA,
                PEGAWAI.TMT_PENSIUN,
                PEGAWAI.GAJI_POKOK,
                PEGAWAI.PHDP,
                PEGAWAI.JML_TANGGUNGAN,
                PEGAWAI.MKG_TAHUN,
                PEGAWAI.MKG_BULAN,
                PEGAWAI.ABSENSI_ID,
                PEGAWAI.EMAIL,
                PEGAWAI.NOTES,
                PEGAWAI.IS_ASKES,
                PEGAWAI.TMT_PEGAWAI,
                BIODATA.NIK.as("biodata_nik"),
                BIODATA.NAMA.as("biodata_nama"),
                BIODATA.JENIS_KELAMIN.as("biodata_jenis_kelamin"),
                BIODATA.TEMPAT_LAHIR.as("biodata_tempat_lahir"),
                BIODATA.TANGGAL_LAHIR.as("biodata_tanggal_lahir"),
                BIODATA.ALAMAT.as("biodata_alamat"),
                BIODATA.TELP.as("biodata_telp"),
                BIODATA.AGAMA.as("biodata_agama"),
                BIODATA.IBU_KANDUNG.as("biodata_ibu_kandung"),
                BIODATA.GOLONGAN_DARAH.as("biodata_golongan_darah"),
                BIODATA.STATUS_KAWIN.as("biodata_status_kawin"),
                BIODATA.FOTO_PROFIL.as("biodata_foto_profil"),
                BIODATA.NOTES.as("biodata_notes"),
                JENJANG_PENDIDIKAN.ID.as("jenjang_id"),
                JENJANG_PENDIDIKAN.NAMA.as("jenjang_nama"),
                JENJANG_PENDIDIKAN.SHORT_NAME.as("jenjang_short_name"),
                JENJANG_PENDIDIKAN.SEQ.as("jenjang_seq"),
                JENJANG_PENDIDIKAN.IS_STATISTIK.as("jenjang_is_statistik"),
                ORGANISASI.ID.as("organisasi_id"),
                ORGANISASI.KODE.as("organisasi_kode"),
                ORGANISASI.NAMA.as("organisasi_nama"),
                ORGANISASI.SHORT_NAME.as("organisasi_short_name"),
                JABATAN.ID.as("jabatan_id"),
                JABATAN.KODE.as("jabatan_kode"),
                JABATAN.NAMA.as("jabatan_nama"),
                LEVEL.ID.as("level_id"),
                LEVEL.NAMA.as("level_nama"),
                PROFESI.ID.as("profesi_id"),
                PROFESI.NAMA.as("profesi_nama"),
                GOLONGAN.ID.as("golongan_id"),
                GOLONGAN.GOLONGAN_.as("golongan_golongan"),
                GOLONGAN.PANGKAT.as("golongan_pangkat"),
                GRADE.ID.as("grade_id"),
                GRADE.GRADE_.as("grade_grade"),
                GRADE.TUKIN.as("grade_tukin"),
                GRADE.LEVEL_ID.as("grade_level_id"),
                select(LEVEL.NAMA).from(LEVEL).where(LEVEL.ID.eq(GRADE.LEVEL_ID)).asField("grade_level_nama"),
                GAJI_PENDAPATAN_NON_PAJAK.ID.as("kode_pajak_id"),
                GAJI_PENDAPATAN_NON_PAJAK.KODE.as("kode_pajak_kode"),
                GAJI_PENDAPATAN_NON_PAJAK.NOMINAL.as("kode_pajak_nominal"),
                GAJI_PENDAPATAN_NON_PAJAK.NOTES.as("kode_pajak_notes"),
                GAJI_PROFIL.ID.as("gaji_profil_id"),
                GAJI_PROFIL.NAMA.as("gaji_profil_nama"),
                RUMAH_DINAS.ID.as("rumah_dinas_id"),
                RUMAH_DINAS.NAMA.as("rumah_dinas_nama"),
                RUMAH_DINAS.NILAI.as("rumah_dinas_nilai")
        };
    }

    /**
     * Fields for the kartu identitas multiset subquery.
     */
    public static Field<?>[] kartuIdentitasFields() {
        return new Field<?>[]{
                KARTU_IDENTITAS.ID,
                KARTU_IDENTITAS.NOMOR_KARTU,
                JENIS_KITAS.ID.as("jenis_kartu_id"),
                JENIS_KITAS.NAMA.as("jenis_kartu_nama")
        };
    }
}
