package id.perumdamts.kepegawaian.repositories.kepegawaian.jooq;

import id.perumdamts.kepegawaian.jooq.tables.Golongan;
import org.jooq.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static id.perumdamts.kepegawaian.jooq.tables.AlasanBerhenti.ALASAN_BERHENTI;
import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.LampiranSk.LAMPIRAN_SK;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.RiwayatSk.RIWAYAT_SK;
import static id.perumdamts.kepegawaian.jooq.tables.RiwayatTerminasi.RIWAYAT_TERMINASI;

/**
 * Single source of truth for the columns of the {@code riwayat_terminasi} read
 * queries: the SELECT list and the {@code RiwayatTerminasiQueryRepository#toQuery}
 * mapper both reference the SAME constants, so SELECT/mapper drift is impossible.
 *
 * <p>Regression guarded here: the SELECT once omitted NAMA_ORGANISASI /
 * NAMA_JABATAN / NAMA_GOLONGAN while the mapper still read them, producing
 * {@code InvalidResultException: Field "riwayat_terminasi"."nama_organisasi" is
 * not contained in row type} on every GET /kepegawaian/riwayat/terminasi.</p>
 */
public final class RiwayatTerminasiSelects {
    private RiwayatTerminasiSelects() {}

    // ── RIWAYAT_TERMINASI columns ─────────────────────────────────────────────
    public static final Field<Long> ID = RIWAYAT_TERMINASI.ID;
    public static final Field<String> NIPAM = RIWAYAT_TERMINASI.NIPAM;
    public static final Field<String> NAMA = RIWAYAT_TERMINASI.NAMA;
    public static final Field<String> NOMOR_SK = RIWAYAT_TERMINASI.NOMOR_SK;
    public static final Field<LocalDate> TANGGAL_TERMINASI = RIWAYAT_TERMINASI.TANGGAL_TERMINASI;
    public static final Field<Integer> TAHUN_TERMINASI = RIWAYAT_TERMINASI.TAHUN_TERMINASI;
    public static final Field<Integer> MASA_KERJA = RIWAYAT_TERMINASI.MASA_KERJA;
    public static final Field<String> NOTES = RIWAYAT_TERMINASI.NOTES;
    public static final Field<String> NAMA_ORGANISASI = RIWAYAT_TERMINASI.NAMA_ORGANISASI;
    public static final Field<String> NAMA_JABATAN = RIWAYAT_TERMINASI.NAMA_JABATAN;
    public static final Field<String> NAMA_GOLONGAN = RIWAYAT_TERMINASI.NAMA_GOLONGAN;

    // ── ALASAN_BERHENTI (alias ab_*) ──────────────────────────────────────────
    public static final Field<Long> AB_ID = ALASAN_BERHENTI.ID.as("ab_id");
    public static final Field<String> AB_NAMA = ALASAN_BERHENTI.NAMA.as("ab_nama");
    public static final Field<String> AB_NOTES = ALASAN_BERHENTI.NOTES.as("ab_notes");

    // ── ORGANISASI (alias org_*) ──────────────────────────────────────────────
    public static final Field<Long> ORG_ID = ORGANISASI.ID.as("org_id");
    public static final Field<String> ORG_NAMA = ORGANISASI.NAMA.as("org_nama");

    // ── JABATAN (alias jab_*) ─────────────────────────────────────────────────
    public static final Field<Long> JAB_ID = JABATAN.ID.as("jab_id");
    public static final Field<String> JAB_NAMA = JABATAN.NAMA.as("jab_nama");

    // ── GOLONGAN (alias gol_*) ────────────────────────────────────────────────
    public static final Field<Long> GOL_ID = GOLONGAN.ID.as("gol_id");
    public static final Field<String> GOL_GOLONGAN = GOLONGAN.GOLONGAN_.as("gol_golongan");
    public static final Field<String> GOL_PANGKAT = GOLONGAN.PANGKAT.as("gol_pangkat");

    // ── RIWAYAT_SK (alias sk_*) ───────────────────────────────────────────────
    public static final Field<Long> SK_ID = RIWAYAT_SK.ID.as("sk_id");
    public static final Field<String> SK_NOMOR = RIWAYAT_SK.NOMOR_SK.as("sk_nomor");
    public static final Field<Byte> SK_JENIS = RIWAYAT_SK.JENIS_SK.as("sk_jenis");
    public static final Field<LocalDate> SK_TGL = RIWAYAT_SK.TANGGAL_SK.as("sk_tgl");
    public static final Field<LocalDate> SK_TMT = RIWAYAT_SK.TMT_BERLAKU.as("sk_tmt");
    public static final Field<Double> SK_GAJI = RIWAYAT_SK.GAJI_POKOK.as("sk_gaji");
    public static final Field<Integer> SK_MKG_T = RIWAYAT_SK.MKG_TAHUN.as("sk_mkg_t");
    public static final Field<Integer> SK_MKG_B = RIWAYAT_SK.MKG_BULAN.as("sk_mkg_b");
    public static final Field<LocalDate> SK_KENAIKAN = RIWAYAT_SK.KENAIKAN_BERIKUTNYA.as("sk_kenaikan");
    public static final Field<Integer> SK_MKGB_T = RIWAYAT_SK.MKGB_TAHUN.as("sk_mkgb_t");
    public static final Field<Integer> SK_MKGB_B = RIWAYAT_SK.MKGB_BULAN.as("sk_mkgb_b");
    public static final Field<Boolean> SK_UPD = RIWAYAT_SK.UPDATE_MASTER.as("sk_upd");
    public static final Field<String> SK_NOTES = RIWAYAT_SK.NOTES.as("sk_notes");

    // ── GOLONGAN self-alias untuk SK (alias sk_gol_*) ─────────────────────────
    public static final Golongan SK_GOL = GOLONGAN.as("sk_gol");
    public static final Field<Long> SK_GOL_ID = SK_GOL.ID.as("sk_gol_id");
    public static final Field<String> SK_GOL_GOLONGAN = SK_GOL.GOLONGAN_.as("sk_gol_golongan");
    public static final Field<String> SK_GOL_PANGKAT = SK_GOL.PANGKAT.as("sk_gol_pangkat");

    // ── LAMPIRAN_SK (alias lam_*) ─────────────────────────────────────────────
    public static final Field<Long> LAM_ID = LAMPIRAN_SK.ID.as("lam_id");
    public static final Field<String> LAM_FILE_NAME = LAMPIRAN_SK.FILE_NAME.as("lam_file_name");
    public static final Field<String> LAM_MIME_TYPE = LAMPIRAN_SK.MIME_TYPE.as("lam_mime_type");
    public static final Field<String> LAM_NOTES = LAMPIRAN_SK.NOTES.as("lam_notes");
    public static final Field<Boolean> LAM_DISETUJUI = LAMPIRAN_SK.DISETUJUI.as("lam_disetujui");
    public static final Field<String> LAM_DISETUJUI_OLEH = LAMPIRAN_SK.DISETUJUI_OLEH.as("lam_disetujui_oleh");
    public static final Field<LocalDateTime> LAM_TGL_DISETUJUI = LAMPIRAN_SK.TANGGAL_DISETUJUI.as("lam_tgl_disetujui");

    /**
     * All columns for the terminasi read queries. Must stay a superset of the
     * constants the {@code toQuery} mapper reads — enforced by
     * {@code RiwayatTerminasiQueryRepositoryTest}.
     */
    static final Field<?>[] QUERY_COLUMNS = new Field<?>[]{
            ID, NIPAM, NAMA, NOMOR_SK, TANGGAL_TERMINASI, TAHUN_TERMINASI, MASA_KERJA, NOTES,
            NAMA_ORGANISASI, NAMA_JABATAN, NAMA_GOLONGAN,
            AB_ID, AB_NAMA, AB_NOTES,
            ORG_ID, ORG_NAMA,
            JAB_ID, JAB_NAMA,
            GOL_ID, GOL_GOLONGAN, GOL_PANGKAT,
            SK_ID, SK_NOMOR, SK_JENIS, SK_TGL, SK_TMT,
            SK_GAJI, SK_MKG_T, SK_MKG_B,
            SK_KENAIKAN, SK_MKGB_T, SK_MKGB_B,
            SK_UPD, SK_NOTES,
            SK_GOL_ID, SK_GOL_GOLONGAN, SK_GOL_PANGKAT,
            LAM_ID, LAM_FILE_NAME, LAM_MIME_TYPE,
            LAM_NOTES, LAM_DISETUJUI, LAM_DISETUJUI_OLEH, LAM_TGL_DISETUJUI
    };
}
