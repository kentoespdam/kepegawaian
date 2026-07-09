package id.perumdamts.kepegawaian.repositories.cuti.jooq;

import id.perumdamts.kepegawaian.jooq.tables.CutiJenis;
import id.perumdamts.kepegawaian.jooq.tables.Jabatan;
import org.jooq.Field;

import static id.perumdamts.kepegawaian.jooq.tables.CutiPegawai.CUTI_PEGAWAI;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;

public final class CutiPegawaiSelects {
    private CutiPegawaiSelects() {}

    // ── Direct CUTI_PEGAWAI columns ──────────────────────────────────────────
    public static final Field<Long> ID = CUTI_PEGAWAI.ID;
    public static final Field<Long> PEGAWAI_ID = CUTI_PEGAWAI.PEGAWAI_ID;
    public static final Field<String> NIPAM = CUTI_PEGAWAI.NIPAM;
    public static final Field<String> NAMA = CUTI_PEGAWAI.NAMA;
    public static final Field<String> PANGKAT_GOLONGAN = CUTI_PEGAWAI.PANGKAT_GOLONGAN;
    public static final Field<java.time.LocalDateTime> CREATED_AT = CUTI_PEGAWAI.CREATED_AT;
    public static final Field<Byte> JENIS_PENGAJUAN_CUTI = CUTI_PEGAWAI.JENIS_PENGAJUAN_CUTI;
    public static final Field<Byte> APPROVAL_CUTI_STATUS = CUTI_PEGAWAI.APPROVAL_CUTI_STATUS;
    public static final Field<Integer> APPROVAL_LEVEL = CUTI_PEGAWAI.APPROVAL_LEVEL;
    public static final Field<java.time.LocalDate> TANGGAL_MULAI = CUTI_PEGAWAI.TANGGAL_MULAI;
    public static final Field<java.time.LocalDate> TANGGAL_SELESAI = CUTI_PEGAWAI.TANGGAL_SELESAI;
    public static final Field<String> ALASAN = CUTI_PEGAWAI.ALASAN;
    public static final Field<Integer> JUMLAH_HARI = CUTI_PEGAWAI.JUMLAH_HARI;
    public static final Field<Integer> JUMLAH_HARI_KERJA = CUTI_PEGAWAI.JUMLAH_HARI_KERJA;
    public static final Field<Byte> IS_CLAIMED = CUTI_PEGAWAI.IS_CLAIMED;
    public static final Field<Long> REF_CUTI_ID = CUTI_PEGAWAI.REF_CUTI_ID;

    // ── ORGANISASI columns (direct join, string-alias prefix `org_*`) ──────
    public static final Field<Long> ORG_ID = ORGANISASI.ID.as("org_id");
    public static final Field<String> ORG_KODE = ORGANISASI.KODE.as("org_kode");
    public static final Field<String> ORG_NAMA = ORGANISASI.NAMA.as("org_nama");

    // ── JABATAN columns (direct join, string-alias prefix `jab_*`) ──────────
    public static final Field<Long> JAB_ID = JABATAN.ID.as("jab_id");
    public static final Field<String> JAB_KODE = JABATAN.KODE.as("jab_kode");
    public static final Field<String> JAB_NAMA = JABATAN.NAMA.as("jab_nama");

    // ── Aliased-column helpers (must pass the aliased table instance) ─────────

    /**
     * Columns for the jenisCuti alias ({@code CUTI_JENIS.as("jc")}).
     * String alias prefix: {@code jc_*}.
     */
    public static Field<?>[] jenisCutiColumns(CutiJenis alias) {
        return new Field<?>[] {
                alias.ID.as("jc_id"),
                alias.NAMA.as("jc_nama")
        };
    }

    /**
     * Columns for the subJenisCuti alias ({@code CUTI_JENIS.as("sjc")}).
     * String alias prefix: {@code sjc_*}.
     */
    public static Field<?>[] subJenisCutiColumns(CutiJenis alias) {
        return new Field<?>[] {
                alias.ID.as("sjc_id"),
                alias.NAMA.as("sjc_nama")
        };
    }

    /**
     * Columns for the pic alias ({@code JABATAN.as("pic")}).
     * String alias prefix: {@code pic_*}.
     */
    public static Field<?>[] picColumns(Jabatan alias) {
        return new Field<?>[] {
                alias.ID.as("pic_id"),
                alias.KODE.as("pic_kode"),
                alias.NAMA.as("pic_nama")
        };
    }

    // ── Combined column arrays ────────────────────────────────────────────────

    /**
     * Full SELECT columns for {@code CutiPengajuanResponse} including {@code refCuti}.
     * Includes all joined-table aliases.
     *
     * @param jenisCutiAlias  aliased table {@code CUTI_JENIS.as("jc")}
     * @param subJenisCutiAlias aliased table {@code CUTI_JENIS.as("sjc")}
     * @param picAlias        aliased table {@code JABATAN.as("pic")}
     */
    public static Field<?>[] fullQueryFields(CutiJenis jenisCutiAlias, CutiJenis subJenisCutiAlias, Jabatan picAlias) {
        return new Field<?>[] {
                ID, PEGAWAI_ID, NIPAM, NAMA, PANGKAT_GOLONGAN, CREATED_AT,
                JENIS_PENGAJUAN_CUTI, APPROVAL_CUTI_STATUS, APPROVAL_LEVEL,
                TANGGAL_MULAI, TANGGAL_SELESAI, ALASAN,
                JUMLAH_HARI, JUMLAH_HARI_KERJA, IS_CLAIMED,
                ORG_ID, ORG_KODE, ORG_NAMA,
                JAB_ID, JAB_KODE, JAB_NAMA,
                jenisCutiAlias.ID.as("jc_id"),
                jenisCutiAlias.NAMA.as("jc_nama"),
                subJenisCutiAlias.ID.as("sjc_id"),
                subJenisCutiAlias.NAMA.as("sjc_nama"),
                picAlias.ID.as("pic_id"),
                picAlias.KODE.as("pic_kode"),
                picAlias.NAMA.as("pic_nama"),
                REF_CUTI_ID
        };
    }

    /**
     * SELECT columns for mini-response ({@code CutiPengajuanMiniResponse}) — same as
     * {@link #fullQueryFields} but without {@code REF_CUTI_ID}.
     */
    public static Field<?>[] miniQueryFields(CutiJenis jenisCutiAlias, CutiJenis subJenisCutiAlias, Jabatan picAlias) {
        return new Field<?>[] {
                ID, PEGAWAI_ID, NIPAM, NAMA, PANGKAT_GOLONGAN, CREATED_AT,
                JENIS_PENGAJUAN_CUTI, APPROVAL_CUTI_STATUS, APPROVAL_LEVEL,
                TANGGAL_MULAI, TANGGAL_SELESAI, ALASAN,
                JUMLAH_HARI, JUMLAH_HARI_KERJA, IS_CLAIMED,
                ORG_ID, ORG_KODE, ORG_NAMA,
                JAB_ID, JAB_KODE, JAB_NAMA,
                jenisCutiAlias.ID.as("jc_id"),
                jenisCutiAlias.NAMA.as("jc_nama"),
                subJenisCutiAlias.ID.as("sjc_id"),
                subJenisCutiAlias.NAMA.as("sjc_nama"),
                picAlias.ID.as("pic_id"),
                picAlias.KODE.as("pic_kode"),
                picAlias.NAMA.as("pic_nama")
        };
    }
}
