package id.perumdamts.kepegawaian.repositories.master.jooq;

import org.jooq.Field;

import static id.perumdamts.kepegawaian.jooq.tables.JenisSp.JENIS_SP;
import static id.perumdamts.kepegawaian.jooq.tables.SanksiSp.SANKSI_SP;

public final class SanksiSelects {
    private SanksiSelects() {}

    public static final Field<Long> ID = SANKSI_SP.ID;
    public static final Field<String> KODE = SANKSI_SP.KODE;
    public static final Field<String> KETERANGAN = SANKSI_SP.KETERANGAN;
    public static final Field<Boolean> POT_TKK = SANKSI_SP.POT_TKK;
    public static final Field<Integer> JML_POT_TKK = SANKSI_SP.JML_POT_TKK;
    public static final Field<Boolean> IS_PENDING_PANGKAT = SANKSI_SP.IS_PENDING_PANGKAT;
    public static final Field<Boolean> IS_PENDING_GAJI = SANKSI_SP.IS_PENDING_GAJI;
    public static final Field<Boolean> IS_TURUN_PANGKAT = SANKSI_SP.IS_TURUN_PANGKAT;
    public static final Field<Boolean> IS_TURUN_JABATAN = SANKSI_SP.IS_TURUN_JABATAN;
    public static final Field<Boolean> IS_SUSPENSION = SANKSI_SP.IS_SUSPENSION;
    public static final Field<Boolean> IS_TERMINATE_DH = SANKSI_SP.IS_TERMINATE_DH;
    public static final Field<Boolean> IS_TERMINATE_TH = SANKSI_SP.IS_TERMINATE_TH;

    public static final Field<Long> JENIS_SP_ID = JENIS_SP.ID.as("jenissp_id");
    public static final Field<String> JENIS_SP_KODE = JENIS_SP.KODE.as("jenissp_kode");
    public static final Field<String> JENIS_SP_NAMA = JENIS_SP.NAMA.as("jenissp_nama");

    static final Field<?>[] SANKSI_QUERY_COLUMNS = new Field[] {
            ID,
            KODE,
            KETERANGAN,
            POT_TKK,
            JML_POT_TKK,
            IS_PENDING_PANGKAT,
            IS_PENDING_GAJI,
            IS_TURUN_PANGKAT,
            IS_TURUN_JABATAN,
            IS_SUSPENSION,
            IS_TERMINATE_DH,
            IS_TERMINATE_TH,
            JENIS_SP_ID,
            JENIS_SP_KODE,
            JENIS_SP_NAMA
    };
}
