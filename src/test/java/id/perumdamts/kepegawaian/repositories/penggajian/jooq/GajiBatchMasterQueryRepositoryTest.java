package id.perumdamts.kepegawaian.repositories.penggajian.jooq;

import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterIndexQuery;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GajiBatchMasterQueryRepositoryTest {

    private final DSLContext dsl = DSL.using(SQLDialect.MARIADB);

    private GajiBatchMasterQueryRepository createRepo(List<String> executedSql) {
        DSLContext captureDsl = DSL.using(new MockConnection(ctx -> {
            executedSql.add(ctx.sql());
            return new MockResult[]{new MockResult(0, dsl.newResult(new Field<?>[0]))};
        }), SQLDialect.MARIADB);
        return new GajiBatchMasterQueryRepository(captureDsl);
    }

    @Test
    void listQueryWithNullStatus_includesIsDeleted_doesNotFilterStatus() {
        List<String> executedSql = new ArrayList<>();
        GajiBatchMasterQueryRepository repo = createRepo(executedSql);

        GajiBatchMasterIndexQuery query = new GajiBatchMasterIndexQuery();
        query.setPeriode("2026-09");
        repo.listQuery(query);

        assertEquals(1, executedSql.size());
        String sql = executedSql.getFirst().toLowerCase();
        assertTrue(sql.contains("join `gaji_batch_root`"), "Must join gaji_batch_root");
        assertTrue(sql.contains("`is_deleted`"), "Must filter is_deleted = false");
        assertTrue(sql.contains("`org_group`"), "Must select org_group from organisasi");
        assertFalse(sql.contains("status >=") || sql.contains("status ="), "Must not filter status when null");
    }

    @Test
    void listQueryWithStatus_filtersGreaterEqualAndNotFailed() {
        List<String> executedSql = new ArrayList<>();
        GajiBatchMasterQueryRepository repo = createRepo(executedSql);

        GajiBatchMasterIndexQuery query = new GajiBatchMasterIndexQuery();
        query.setPeriode("2026-09");
        query.setStatus(EProsesGaji.WAIT_APPROVAL);
        repo.listQuery(query);

        assertEquals(1, executedSql.size());
        String sql = executedSql.getFirst().toLowerCase();
        assertTrue(sql.contains("join `gaji_batch_root`"), "Must join gaji_batch_root");
        assertTrue(sql.contains("`is_deleted`"), "Must filter is_deleted = false");
        assertTrue(sql.contains("`status` >="), "Must filter status >= param");
        assertTrue(sql.contains("`status` <>") || sql.contains("`status` !="), "Must exclude FAILED");
    }

    @Test
    void listQueryWithStatusFailed_filtersExactFailed() {
        List<String> executedSql = new ArrayList<>();
        GajiBatchMasterQueryRepository repo = createRepo(executedSql);

        GajiBatchMasterIndexQuery query = new GajiBatchMasterIndexQuery();
        query.setPeriode("2026-09");
        query.setStatus(EProsesGaji.FAILED);
        repo.listQuery(query);

        assertEquals(1, executedSql.size());
        String sql = executedSql.getFirst().toLowerCase();
        assertTrue(sql.contains("join `gaji_batch_root`"), "Must join gaji_batch_root");
        assertTrue(sql.contains("`status` ="), "Must filter exact status = FAILED");
        assertFalse(sql.contains("`status` >="), "Must not use >= for FAILED");
    }

    @Test
    void findByPegawaiId_ignoresQueryStatusAndLocksToFinished() {
        List<String> executedSql = new ArrayList<>();
        GajiBatchMasterQueryRepository repo = createRepo(executedSql);

        GajiBatchMasterIndexQuery query = new GajiBatchMasterIndexQuery();
        query.setPeriode("2026-09");
        query.setStatus(EProsesGaji.PENDING); // Should be ignored
        repo.findByPegawaiId(123L, query);

        // findByPegawaiId runs count + select
        assertEquals(2, executedSql.size());
        for (String s : executedSql) {
            String sql = s.toLowerCase();
            assertTrue(sql.contains("`pegawai_id` = ?"), "Must filter by pegawaiId");
            assertTrue(sql.contains("`status` >="), "Must lock to >= FINISHED");
            assertFalse(sql.contains("`status` = ?"), "Must not use query.status");
        }
    }
}
