package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.jooq.tables.Jabatan;
import org.jooq.Field;

import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;

public final class JabatanSelects {
    private JabatanSelects() {}

    public static final Field<Long> ID = JABATAN.ID;
    public static final Field<String> KODE = JABATAN.KODE;
    public static final Field<String> NAMA = JABATAN.NAMA;

    /**
     * ⚠️ Static parent field — referensi ke main table, BUKAN alias self-join.
     * Hanya aman untuk query tanpa self-join. Untuk self-join gunakan {@link #parentColumns(Jabatan)}.
     */
    public static final Field<Long> PARENT_ID = JABATAN.ID.as("parent_id");
    public static final Field<String> PARENT_KODE = JABATAN.KODE.as("parent_kode");
    public static final Field<String> PARENT_NAMA = JABATAN.NAMA.as("parent_nama");

    static final Field<?>[] JABATAN_COLUMNS = new Field[] {
            ID,
            KODE,
            NAMA
    };

    /**
     * Parent columns ter-kualifikasi dengan alias self-join.
     * Gunakan di repository query dengan self-join:
     * <pre>{@code
     * var parent = JABATAN.as("parent");
     * dsl.select(JabatanSelects.parentColumns(parent)).from(JABATAN).leftJoin(parent)...
     * }</pre>
     */
    public static Field<?>[] parentColumns(Jabatan parentAlias) {
        return new Field<?>[] {
                parentAlias.ID.as("parent_id"),
                parentAlias.KODE.as("parent_kode"),
                parentAlias.NAMA.as("parent_nama")
        };
    }
}
