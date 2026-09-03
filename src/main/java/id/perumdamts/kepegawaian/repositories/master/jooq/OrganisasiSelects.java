package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.jooq.tables.Organisasi;
import org.jooq.Field;

import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;

public final class OrganisasiSelects {
    private OrganisasiSelects() {}

    public static final Field<Long> ID = ORGANISASI.ID;
    public static final Field<String> KODE = ORGANISASI.KODE;
    public static final Field<Integer> LEVEL_ORG = ORGANISASI.LEVEL_ORG;
    public static final Field<String> NAMA = ORGANISASI.NAMA;
    public static final Field<String> SHORT_NAME = ORGANISASI.SHORT_NAME;
    public static final Field<String> CATEGORY = ORGANISASI.CATEGORY;
    public static final Field<String> GROUP = ORGANISASI.ORG_GROUP;

    /**
     * ⚠️ Static parent field — referensi ke main table, BUKAN alias self-join.
     * Hanya aman untuk query tanpa self-join. Untuk self-join gunakan {@link #parentColumns(Organisasi)}.
     */
    public static final Field<Long> PARENT_ID = ORGANISASI.ID.as("parent_id");
    public static final Field<String> PARENT_KODE = ORGANISASI.KODE.as("parent_kode");
    public static final Field<String> PARENT_NAMA = ORGANISASI.NAMA.as("parent_nama");
    public static final Field<String> PARENT_SHORT_NAME = ORGANISASI.SHORT_NAME.as("parent_short_name");

    static final Field<?>[] ORGANISASI_COLUMNS = new Field[] {
            ID,
            KODE,
            LEVEL_ORG,
            NAMA,
            SHORT_NAME,
            CATEGORY,
            GROUP
    };

    /**
     * Parent columns ter-kualifikasi dengan alias self-join.
     * Gunakan ini di repository query dengan self-join:
     * <pre>{@code
     * var parent = ORGANISASI.as("parent");
     * dsl.select(OrganisasiSelects.parentColumns(parent)).from(ORGANISASI).leftJoin(parent)...
     * }</pre>
     */
    public static Field<?>[] parentColumns(Organisasi parentAlias) {
        return new Field<?>[] {
                parentAlias.ID.as("parent_id"),
                parentAlias.KODE.as("parent_kode"),
                parentAlias.NAMA.as("parent_nama"),
                parentAlias.SHORT_NAME.as("parent_short_name")
        };
    }
}
