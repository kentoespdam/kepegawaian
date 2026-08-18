package id.perumdamts.kepegawaian.repositories.cuti.jooq;

import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisListRequest;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisMiniResponse;
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

import static id.perumdamts.kepegawaian.jooq.tables.CutiJenis.CUTI_JENIS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression (2026-08-18): GET /cuti/jenis/list kini mengembalikan
 * {@link CutiJenisMiniResponse} {id,nama,parentId} langsung — parentId di-select
 * langsung dari kolom parent_id (nilai riil, null hanya untuk root), tanpa
 * self-join + parent nested (dulu: parentId mini selalu null di jalur JOOQ).
 * Mengikuti pola {@code RiwayatTerminasiQueryRepositoryTest} (jOOQ MockConnection,
 * tanpa database).
 */
class CutiJenisQueryRepositoryTest {

    private final DSLContext dsl = DSL.using(SQLDialect.MARIADB);

    /** Mirrors the columns of listQuery's SELECT, in the same order. */
    private Record newRow() {
        return dsl.newRecord(CUTI_JENIS.ID, CUTI_JENIS.NAMA, CUTI_JENIS.PARENT_ID);
    }

    private DSLContext mockDsl(Result<Record> rows) {
        MockDataProvider provider = ctx -> new MockResult[]{new MockResult(1, rows)};
        return DSL.using(new MockConnection(provider), SQLDialect.MARIADB);
    }

    @Test
    void listQueryReturnsMiniResponseWithRealParentId() {
        Result<Record> rows = dsl.newResult(newRow().fields());
        Record sub = newRow();
        sub.set(CUTI_JENIS.ID, 2L);
        sub.set(CUTI_JENIS.NAMA, "Cuti Sakit");
        sub.set(CUTI_JENIS.PARENT_ID, 1L);
        rows.add(sub);

        CutiJenisQueryRepository repo = new CutiJenisQueryRepository(mockDsl(rows));

        List<CutiJenisMiniResponse> list = repo.listQuery(new CutiJenisListRequest());

        assertEquals(1, list.size());
        CutiJenisMiniResponse mini = list.getFirst();
        assertEquals(2L, mini.id());
        assertEquals("Cuti Sakit", mini.nama());
        assertEquals(1L, mini.parentId(),
                "parentId harus nilai riil dari kolom parent_id, bukan null (regression: dulu null via parent nested)");
    }

    @Test
    void listQueryRootJenisHasNullParentId() {
        Result<Record> rows = dsl.newResult(newRow().fields());
        Record root = newRow();
        root.set(CUTI_JENIS.ID, 1L);
        root.set(CUTI_JENIS.NAMA, "Cuti Tahunan");
        root.set(CUTI_JENIS.PARENT_ID, (Long) null);
        rows.add(root);

        CutiJenisQueryRepository repo = new CutiJenisQueryRepository(mockDsl(rows));

        List<CutiJenisMiniResponse> list = repo.listQuery(new CutiJenisListRequest());

        assertEquals(1, list.size());
        assertNull(list.getFirst().parentId(), "root jenis harus membawa parentId null");
    }

    @Test
    void listQuerySelectsParentIdDirectlyWithoutSelfJoin() {
        List<String> executedSql = new ArrayList<>();
        DSLContext captureDsl = DSL.using(new MockConnection(ctx -> {
            executedSql.add(ctx.sql());
            return new MockResult[]{new MockResult(0, dsl.newResult(new Field<?>[0]))};
        }), SQLDialect.MARIADB);
        CutiJenisQueryRepository repo = new CutiJenisQueryRepository(captureDsl);

        repo.listQuery(new CutiJenisListRequest());

        assertEquals(1, executedSql.size(), "listQuery adalah satu query (tanpa count)");
        String sql = executedSql.get(0).toLowerCase();
        assertTrue(sql.contains("parent_id"), "SELECT harus memuat kolom parent_id (di-select langsung)");
        assertFalse(sql.contains("join"),
                "TIDAK boleh ada self-join ke parent — dulu left join parent menghasilkan parentId selalu null");
    }
}
