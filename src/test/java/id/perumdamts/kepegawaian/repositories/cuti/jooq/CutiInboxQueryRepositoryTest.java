package id.perumdamts.kepegawaian.repositories.cuti.jooq;

import id.perumdamts.kepegawaian.dto.cuti.approvalChain.CutiApprovalChainRequest;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
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

/**
 * Regression (2026-08-18): GET /cuti/pengajuan/approval 500 "bad SQL grammar" —
 * filter jabatan (picSaatIniId) dan read_write_status dirender sebagai
 * {@code cuti_approval_chain.jabatan_id} di WHERE query luar, padahal tabel chain
 * hanya ada di dalam derived table {@code ranked} (out of scope). Filter chain
 * harus dipindah ke DALAM subquery {@code ranked} (sebelum ROW_NUMBER, sesuai
 * decisions-cuti.md), dan ORDER BY harus di-qualify ke {@code ranked}. Mengikuti
 * pola {@code CutiJenisQueryRepositoryTest} (jOOQ MockConnection, tanpa database).
 */
class CutiInboxQueryRepositoryTest {

    private final DSLContext dsl = DSL.using(SQLDialect.MARIADB);

    @Test
    void pageQueryKeepsChainFiltersInsideRankedSubquery() {
        List<String> executedSql = new ArrayList<>();
        DSLContext captureDsl = DSL.using(new MockConnection(ctx -> {
            executedSql.add(ctx.sql());
            return new MockResult[]{new MockResult(0, dsl.newResult(new Field<?>[0]))};
        }), SQLDialect.MARIADB);
        CutiInboxQueryRepository repo = new CutiInboxQueryRepository(captureDsl);

        CutiApprovalChainRequest req = new CutiApprovalChainRequest();
        req.setTahun(2026);
        req.setPicSaatIniId(890300426L);
        req.setApprovalCutiStatus(EApprovalCutiStatus.PENDING);

        repo.pageQuery(req);

        assertEquals(2, executedSql.size(), "count + data = 2 query");
        String dataSql = executedSql.get(1).toLowerCase();

        // Derived table diakhiri ") as `ranked`" — semua teks setelahnya adalah query luar
        int rankedEnd = dataSql.indexOf(") as `ranked`");
        assertTrue(rankedEnd > 0, "data query harus memuat derived table ranked");

        String outer = dataSql.substring(rankedEnd);
        String inner = dataSql.substring(0, rankedEnd);

        assertFalse(outer.contains("cuti_approval_chain"),
                "query luar TIDAK boleh mereferensikan tabel cuti_approval_chain (regression bad SQL grammar)");
        assertTrue(outer.contains("`ranked`.`rn` = ?"),
                "query luar harus memfilter rn = 1 pada derived table ranked");

        assertTrue(inner.contains("`cuti_approval_chain`.`jabatan_id`"),
                "filter jabatan (picSaatIniId) harus berada DI DALAM subquery ranked (sebelum ROW_NUMBER)");
        assertTrue(inner.contains("row_number() over (partition by `cuti_approval_chain`.`ref_cuti_id`"),
                "ROW_NUMBER partition by ref_cuti_id harus di dalam subquery ranked");

        assertTrue(dataSql.contains("order by `ranked`.`id`"),
                "ORDER BY default harus di-qualify ke ranked, bukan cuti_approval_chain (regression bad SQL grammar)");
    }

    @Test
    void pageQueryAppliesPegawaiFiltersOnOuterQuery() {
        List<String> executedSql = new ArrayList<>();
        DSLContext captureDsl = DSL.using(new MockConnection(ctx -> {
            executedSql.add(ctx.sql());
            return new MockResult[]{new MockResult(0, dsl.newResult(new Field<?>[0]))};
        }), SQLDialect.MARIADB);
        CutiInboxQueryRepository repo = new CutiInboxQueryRepository(captureDsl);

        CutiApprovalChainRequest req = new CutiApprovalChainRequest();
        req.setTahun(2026);
        req.setPicSaatIniId(890300426L);
        req.setApprovalCutiStatus(EApprovalCutiStatus.PENDING);

        repo.pageQuery(req);

        String dataSql = executedSql.get(1).toLowerCase();
        int rankedEnd = dataSql.indexOf(") as `ranked`");
        String outer = dataSql.substring(rankedEnd);

        assertTrue(outer.contains("`cuti_pegawai`.`is_deleted` = ?"),
                "filter is_deleted harus di query luar (tabel cuti_pegawai di-join di sana)");
        assertTrue(outer.contains("`cuti_pegawai`.`approval_cuti_status` = ?"),
                "filter approvalCutiStatus harus di query luar");
        assertTrue(outer.contains("extract(year from `cuti_pegawai`.`created_at`)"),
                "filter tahun harus di query luar via cuti_pegawai.created_at");
    }
}
