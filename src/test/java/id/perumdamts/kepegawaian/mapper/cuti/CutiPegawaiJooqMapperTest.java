package id.perumdamts.kepegawaian.mapper.cuti;

import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanMiniResponse;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static id.perumdamts.kepegawaian.jooq.tables.CutiPegawai.CUTI_PEGAWAI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Regression (FE-CONTRACT-cuti-jenis-mini-parentid): {@code CutiJenisMiniResponse} kini
 * membawa {@code parentId}. Jalur JOOQ: {@code populateFromRecord} mengisi
 * {@code subJenisCuti.parentId} dari kolom alias {@code jc_id} (parent sub-jenis = jenis
 * cuti, konsisten dengan jalur entity {@code CutiPengajuanResponse.from}).
 * {@code mapToResponse} mendelegasi ke {@code populateFromRecord} yang sama.
 * Mengikuti pola mapper-JOOQ test lain (JOOQ mocking, tanpa DB).
 */
class CutiPegawaiJooqMapperTest {

    private final DSLContext dsl = DSL.using(SQLDialect.MARIADB);

    private static final Field<Long> ORG_ID = DSL.field("org_id", Long.class);
    private static final Field<String> ORG_KODE = DSL.field("org_kode", String.class);
    private static final Field<String> ORG_NAMA = DSL.field("org_nama", String.class);
    private static final Field<Long> JAB_ID = DSL.field("jab_id", Long.class);
    private static final Field<String> JAB_KODE = DSL.field("jab_kode", String.class);
    private static final Field<String> JAB_NAMA = DSL.field("jab_nama", String.class);
    private static final Field<Long> JC_ID = DSL.field("jc_id", Long.class);
    private static final Field<String> JC_NAMA = DSL.field("jc_nama", String.class);
    private static final Field<Long> SJC_ID = DSL.field("sjc_id", Long.class);
    private static final Field<String> SJC_NAMA = DSL.field("sjc_nama", String.class);
    private static final Field<Long> PIC_ID = DSL.field("pic_id", Long.class);
    private static final Field<String> PIC_KODE = DSL.field("pic_kode", String.class);
    private static final Field<String> PIC_NAMA = DSL.field("pic_nama", String.class);

    private Record newRow() {
        return dsl.newRecord(
                CUTI_PEGAWAI.ID, CUTI_PEGAWAI.PEGAWAI_ID, CUTI_PEGAWAI.NAMA, CUTI_PEGAWAI.NIPAM,
                CUTI_PEGAWAI.PANGKAT_GOLONGAN, CUTI_PEGAWAI.CREATED_AT,
                CUTI_PEGAWAI.JENIS_PENGAJUAN_CUTI, CUTI_PEGAWAI.APPROVAL_CUTI_STATUS,
                CUTI_PEGAWAI.APPROVAL_LEVEL, CUTI_PEGAWAI.TANGGAL_MULAI, CUTI_PEGAWAI.TANGGAL_SELESAI,
                CUTI_PEGAWAI.ALASAN, CUTI_PEGAWAI.JUMLAH_HARI, CUTI_PEGAWAI.JUMLAH_HARI_KERJA,
                CUTI_PEGAWAI.IS_CLAIMED,
                ORG_ID, ORG_KODE, ORG_NAMA, JAB_ID, JAB_KODE, JAB_NAMA,
                JC_ID, JC_NAMA, SJC_ID, SJC_NAMA, PIC_ID, PIC_KODE, PIC_NAMA);
    }

    @Test
    void subJenisCutiMembawaParentIdJenisCuti() {
        Record row = newRow();
        row.set(CUTI_PEGAWAI.ID, 100L);
        row.set(CUTI_PEGAWAI.PEGAWAI_ID, 9L);
        row.set(CUTI_PEGAWAI.NAMA, "Pegawai Test");
        row.set(CUTI_PEGAWAI.NIPAM, "830100446");
        row.set(CUTI_PEGAWAI.PANGKAT_GOLONGAN, "III/a - Penata Muda");
        row.set(CUTI_PEGAWAI.CREATED_AT, LocalDateTime.of(2026, 8, 1, 9, 0));
        row.set(CUTI_PEGAWAI.JENIS_PENGAJUAN_CUTI, (byte) 0);
        row.set(CUTI_PEGAWAI.APPROVAL_CUTI_STATUS, (byte) 0);
        row.set(CUTI_PEGAWAI.APPROVAL_LEVEL, 1);
        row.set(CUTI_PEGAWAI.TANGGAL_MULAI, LocalDate.of(2026, 8, 10));
        row.set(CUTI_PEGAWAI.TANGGAL_SELESAI, LocalDate.of(2026, 8, 12));
        row.set(CUTI_PEGAWAI.ALASAN, "izin");
        row.set(CUTI_PEGAWAI.JUMLAH_HARI, 3);
        row.set(CUTI_PEGAWAI.JUMLAH_HARI_KERJA, 3);
        row.set(CUTI_PEGAWAI.IS_CLAIMED, (byte) 0);
        row.set(JC_ID, 1L);
        row.set(JC_NAMA, "Cuti Tahunan");
        row.set(SJC_ID, 2L);
        row.set(SJC_NAMA, "Cuti Sakit");

        CutiPengajuanMiniResponse mini = CutiPegawaiJooqMapper.mapToMiniResponse(row);

        assertEquals(2L, mini.subJenisCuti().id());
        assertEquals(1L, mini.jenisCuti().id());
        assertEquals(mini.jenisCuti().id(), mini.subJenisCuti().parentId(),
                "subJenisCuti.parentId harus sama dengan jenisCuti.id (jalur JOOQ)");
        assertNull(mini.jenisCuti().parentId(),
                "jenisCuti tanpa kolom parent di query harus membawa parentId null");
    }

    @Test
    void subJenisTanpaJenisCutiMembawaParentIdNull() {
        Record row = newRow();
        row.set(CUTI_PEGAWAI.ID, 101L);
        row.set(SJC_ID, 2L);
        row.set(SJC_NAMA, "Cuti Sakit");

        CutiPengajuanMiniResponse mini = CutiPegawaiJooqMapper.mapToMiniResponse(row);

        assertNotNull(mini.subJenisCuti(), "subJenisCuti tetap terbentuk walau jc_id null");
        assertNull(mini.subJenisCuti().parentId(),
                "parentId harus null saat jc_id tidak ada di record (bukan NPE)");
    }
}
