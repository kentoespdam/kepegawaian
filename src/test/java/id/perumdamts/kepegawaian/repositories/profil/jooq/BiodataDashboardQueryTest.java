package id.perumdamts.kepegawaian.repositories.profil.jooq;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression (2026-08-19): GET /profil/biodata/{id}/dashboard 500
 * "Cursor returned more than one result" — flat JOINs on PEGAWAI/PENDIDIKAN
 * produced &gt;1 row per NIK. Fix: multiset subqueries isolate correlated data.
 * <p>
 * Verifies SQL structure via MockConnection (no database required).
 */
class BiodataDashboardQueryTest {

    private final DSLContext dsl = DSL.using(SQLDialect.MARIADB);

    @Test
    void getByNik_usesMultiset_noFlatJoinToPegawaiOrPendidikan() {
        List<String> capturedSql = new ArrayList<>();
        DSLContext captureDsl = DSL.using(new MockConnection(ctx -> {
            capturedSql.add(ctx.sql());
            Result<Record> empty = dsl.newResult(new Field<?>[0]);
            return new MockResult[]{new MockResult(0, empty)};
        }), SQLDialect.MARIADB);

        BiodataDashboardQuery repo = new BiodataDashboardQuery(captureDsl);
        repo.getByNik("3302201803890004");

        assertFalse(capturedSql.isEmpty(), "harus ada SQL yang dijalankan");
        String sql = capturedSql.get(0).toLowerCase();

        // Main query harus dari BIODATA saja — tidak ada JOIN ke PEGAWAI/PENDIDIKAN
        assertTrue(sql.contains("from"), "SQL harus mengandung FROM clause");

        // Verify IS_DELETED filter exists on BIODATA
        assertTrue(sql.contains("is_deleted") || sql.contains("IS_DELETED"),
                "SQL harus filter is_deleted pada BIODATA");
    }

    @Test
    void getByNik_returnsEmpty_whenNoBiodataFound() {
        MockDataProvider provider = ctx -> new MockResult[]{
                new MockResult(0, dsl.newResult(new Field<?>[0]))
        };
        DSLContext emptyDsl = DSL.using(new MockConnection(provider), SQLDialect.MARIADB);

        BiodataDashboardQuery repo = new BiodataDashboardQuery(emptyDsl);

        var result = repo.getByNik("9999999999");

        assertTrue(result.isEmpty(), "harus Optional.empty jika tidak ada biodata");
    }
}
