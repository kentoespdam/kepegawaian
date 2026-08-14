package id.perumdamts.kepegawaian.repositories.kepegawaian.jooq;

import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiQuery;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiRequest;
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
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static id.perumdamts.kepegawaian.jooq.tables.AlasanBerhenti.ALASAN_BERHENTI;
import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.LampiranSk.LAMPIRAN_SK;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.RiwayatSk.RIWAYAT_SK;
import static id.perumdamts.kepegawaian.jooq.tables.RiwayatTerminasi.RIWAYAT_TERMINASI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression for GET /kepegawaian/riwayat/terminasi returning 500:
 * {@code Field "riwayat_terminasi"."nama_organisasi" is not contained in row type ...}
 *
 * <p>{@code toQuery()} reads the snapshot labels NAMA_ORGANISASI / NAMA_JABATAN /
 * NAMA_GOLONGAN from the fetched row, but neither {@code pageQuery()} nor
 * {@code getById()} SELECTs them — jOOQ throws {@code InvalidResultException}
 * while mapping every row.</p>
 *
 * <p>The row type built here must mirror the columns the SELECT produces
 * (including the three snapshot labels). Runs the real query construction +
 * mapper chain via jOOQ MockConnection — no database required.</p>
 */
class RiwayatTerminasiQueryRepositoryTest {

    private final DSLContext dsl = DSL.using(SQLDialect.MARIADB);

    /** Mirrors the columns of the query's SELECT, in the same order/aliases. */
    private Record newRow() {
        var skGol = GOLONGAN.as("sk_gol");
        return dsl.newRecord(
                RIWAYAT_TERMINASI.ID,
                RIWAYAT_TERMINASI.NIPAM,
                RIWAYAT_TERMINASI.NAMA,
                RIWAYAT_TERMINASI.NOMOR_SK,
                RIWAYAT_TERMINASI.TANGGAL_TERMINASI,
                RIWAYAT_TERMINASI.TAHUN_TERMINASI,
                RIWAYAT_TERMINASI.MASA_KERJA,
                RIWAYAT_TERMINASI.NOTES,
                RIWAYAT_TERMINASI.NAMA_ORGANISASI,
                RIWAYAT_TERMINASI.NAMA_JABATAN,
                RIWAYAT_TERMINASI.NAMA_GOLONGAN,
                ALASAN_BERHENTI.ID.as("ab_id"), ALASAN_BERHENTI.NAMA.as("ab_nama"), ALASAN_BERHENTI.NOTES.as("ab_notes"),
                ORGANISASI.ID.as("org_id"), ORGANISASI.NAMA.as("org_nama"),
                JABATAN.ID.as("jab_id"), JABATAN.NAMA.as("jab_nama"),
                GOLONGAN.ID.as("gol_id"), GOLONGAN.GOLONGAN_.as("gol_golongan"), GOLONGAN.PANGKAT.as("gol_pangkat"),
                RIWAYAT_SK.ID.as("sk_id"), RIWAYAT_SK.NOMOR_SK.as("sk_nomor"), RIWAYAT_SK.JENIS_SK.as("sk_jenis"),
                RIWAYAT_SK.TANGGAL_SK.as("sk_tgl"), RIWAYAT_SK.TMT_BERLAKU.as("sk_tmt"),
                RIWAYAT_SK.GAJI_POKOK.as("sk_gaji"), RIWAYAT_SK.MKG_TAHUN.as("sk_mkg_t"), RIWAYAT_SK.MKG_BULAN.as("sk_mkg_b"),
                RIWAYAT_SK.KENAIKAN_BERIKUTNYA.as("sk_kenaikan"), RIWAYAT_SK.MKGB_TAHUN.as("sk_mkgb_t"), RIWAYAT_SK.MKGB_BULAN.as("sk_mkgb_b"),
                RIWAYAT_SK.UPDATE_MASTER.as("sk_upd"), RIWAYAT_SK.NOTES.as("sk_notes"),
                skGol.ID.as("sk_gol_id"), skGol.GOLONGAN_.as("sk_gol_golongan"), skGol.PANGKAT.as("sk_gol_pangkat"),
                LAMPIRAN_SK.ID.as("lam_id"), LAMPIRAN_SK.FILE_NAME.as("lam_file_name"), LAMPIRAN_SK.MIME_TYPE.as("lam_mime_type"),
                LAMPIRAN_SK.NOTES.as("lam_notes"), LAMPIRAN_SK.DISETUJUI.as("lam_disetujui"),
                LAMPIRAN_SK.DISETUJUI_OLEH.as("lam_disetujui_oleh"), LAMPIRAN_SK.TANGGAL_DISETUJUI.as("lam_tgl_disetujui")
        );
    }

    private Record populatedRow() {
        Record row = newRow();
        row.set(RIWAYAT_TERMINASI.ID, 10L);
        row.set(RIWAYAT_TERMINASI.NIPAM, "890300426");
        row.set(RIWAYAT_TERMINASI.NAMA, "ABDUL AZIZ MIFTAHUDDIN, S.Kom.");
        row.set(RIWAYAT_TERMINASI.NOMOR_SK, "SK/2024/001");
        row.set(RIWAYAT_TERMINASI.TANGGAL_TERMINASI, LocalDate.of(2024, 6, 30));
        row.set(RIWAYAT_TERMINASI.TAHUN_TERMINASI, 2024);
        row.set(RIWAYAT_TERMINASI.MASA_KERJA, 15);
        row.set(RIWAYAT_TERMINASI.NOTES, "pensiun");
        row.set(RIWAYAT_TERMINASI.NAMA_ORGANISASI, "SUB BAG TEKNOLOGI INFORMASI");
        row.set(RIWAYAT_TERMINASI.NAMA_JABATAN, "Supervisor Teknologi Informasi");
        row.set(RIWAYAT_TERMINASI.NAMA_GOLONGAN, "Pembina - IV/a");
        return row;
    }

    private DSLContext mockDsl(Result<Record> rows) {
        MockDataProvider provider = ctx -> {
            if (ctx.sql().toLowerCase().startsWith("select count")) {
                Field<Long> count = DSL.field("cnt", Long.class);
                Result<Record> countRows = dsl.newResult(new Field<?>[]{count});
                countRows.add(dsl.newRecord(count).value1(1L));
                return new MockResult[]{new MockResult(1, countRows)};
            }
            return new MockResult[]{new MockResult(1, rows)};
        };
        return DSL.using(new MockConnection(provider), SQLDialect.MARIADB);
    }

    @Test
    void pageQueryMapsSnapshotLabelColumns() {
        Result<Record> rows = dsl.newResult(populatedRow().fields());
        rows.add(populatedRow());
        RiwayatTerminasiQueryRepository repo = new RiwayatTerminasiQueryRepository(mockDsl(rows));

        Page<RiwayatTerminasiQuery> page = repo.pageQuery(new RiwayatTerminasiRequest());

        assertEquals(1, page.getTotalElements());
        RiwayatTerminasiQuery q = page.getContent().getFirst();
        assertNotNull(q);
        assertEquals(10L, q.id());
        assertEquals("SUB BAG TEKNOLOGI INFORMASI", q.namaOrganisasi());
        assertEquals("Supervisor Teknologi Informasi", q.namaJabatan());
        assertEquals("Pembina - IV/a", q.namaGolongan());
    }

    @Test
    void pageQueryAppliesAllDeclaredRequestFilters() {
        List<String> executedSql = new ArrayList<>();
        DSLContext captureDsl = DSL.using(new MockConnection(ctx -> {
            executedSql.add(ctx.sql());
            if (ctx.sql().toLowerCase().startsWith("select count")) {
                Field<Long> count = DSL.field("cnt", Long.class);
                Result<Record> countRows = dsl.newResult(new Field<?>[]{count});
                countRows.add(dsl.newRecord(count).value1(1L));
                return new MockResult[]{new MockResult(1, countRows)};
            }
            return new MockResult[]{new MockResult(0, dsl.newResult(new Field<?>[0]))};
        }), SQLDialect.MARIADB);
        RiwayatTerminasiQueryRepository repo = new RiwayatTerminasiQueryRepository(captureDsl);

        RiwayatTerminasiRequest request = new RiwayatTerminasiRequest();
        request.setPegawaiId(77L);
        request.setNipam("890300426");
        request.setNama("ABDUL");
        request.setAlasanTerminasiId(5L);
        request.setJabatanId(3L);
        request.setOrganisasiId(9L);
        request.setGolonganId(2L);
        request.setNomorSk("SK/2024");
        request.setTahunPensiun(2024);
        request.setTanggalTerminasi(LocalDate.of(2024, 6, 30));

        repo.pageQuery(request);

        // [count, data] — periksa WHERE clause dari query data (SELECT juga punya kolom nama dll.,
        // jadi hanya bagian WHERE yang diassert agar tidak false-positive).
        assertEquals(2, executedSql.size());
        String dataSql = executedSql.get(1);
        String where = dataSql.toLowerCase().substring(dataSql.toLowerCase().indexOf("where"));
        assertTrue(where.contains("pegawai_id"));
        assertTrue(where.contains("nipam"));
        assertTrue(where.contains("nama"));
        assertTrue(where.contains("alasan_terminasi_id"));
        assertTrue(where.contains("jabatan_id"));
        assertTrue(where.contains("organisasi_id"));
        assertTrue(where.contains("golongan_id"));
        assertTrue(where.contains("nomor_sk"));
        assertTrue(where.contains("tahun_terminasi"));
        assertTrue(where.contains("tanggal_terminasi"));
    }

    @Test
    void getByIdMapsSnapshotLabelColumns() {
        Result<Record> rows = dsl.newResult(populatedRow().fields());
        rows.add(populatedRow());
        RiwayatTerminasiQueryRepository repo = new RiwayatTerminasiQueryRepository(mockDsl(rows));

        RiwayatTerminasiQuery q = repo.getById(10L).orElseThrow();

        assertEquals(10L, q.id());
        assertEquals("SUB BAG TEKNOLOGI INFORMASI", q.namaOrganisasi());
        assertEquals("Supervisor Teknologi Informasi", q.namaJabatan());
        assertEquals("Pembina - IV/a", q.namaGolongan());
    }
}
