package id.perumdamts.kepegawaian.mapper.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootResponse;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import org.jooq.Record;
import org.junit.jupiter.api.Test;

import static id.perumdamts.kepegawaian.jooq.tables.GajiBatchRoot.GAJI_BATCH_ROOT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Regression test: GajiBatchRootJooqMapper must convert INTEGER status column
 * to EProsesGaji enum via ordinal indexing.
 */
class GajiBatchRootJooqMapperTest {

    @Test
    void mapStatus_allOrdinalsConvertCorrectly() {
        for (EProsesGaji expected : EProsesGaji.values()) {
            Record record = mock(Record.class);
            when(record.get(GAJI_BATCH_ROOT.STATUS)).thenReturn(expected.ordinal());
            when(record.get(GAJI_BATCH_ROOT.ID)).thenReturn("202609-001");
            when(record.get(GAJI_BATCH_ROOT.PERIODE)).thenReturn("202609");
            when(record.get(GAJI_BATCH_ROOT.TOTAL_PEGAWAI)).thenReturn(10);
            when(record.get(GAJI_BATCH_ROOT.TANGGAL_PROSES)).thenReturn(null);
            when(record.get(GAJI_BATCH_ROOT.DI_PROSES_OLEH)).thenReturn(null);
            when(record.get(GAJI_BATCH_ROOT.JABATAN_PEMROSES)).thenReturn(null);
            when(record.get(GAJI_BATCH_ROOT.TANGGAL_VERIFIKASI_TAHAP1)).thenReturn(null);
            when(record.get(GAJI_BATCH_ROOT.DI_VERIFIKASI_OLEH_TAHAP1)).thenReturn(null);
            when(record.get(GAJI_BATCH_ROOT.JABATAN_VERIFIKASI_TAHAP1)).thenReturn(null);
            when(record.get(GAJI_BATCH_ROOT.TANGGAL_VERIFIKASI_TAHAP2)).thenReturn(null);
            when(record.get(GAJI_BATCH_ROOT.DI_VERIFIKASI_OLEH_TAHAP2)).thenReturn(null);
            when(record.get(GAJI_BATCH_ROOT.JABATAN_VERIFIKASI_TAHAP2)).thenReturn(null);
            when(record.get(GAJI_BATCH_ROOT.TANGGAL_PERSETUJUAN)).thenReturn(null);
            when(record.get(GAJI_BATCH_ROOT.DI_SETUJUI_OLEH)).thenReturn(null);
            when(record.get(GAJI_BATCH_ROOT.JABATAN_PENYETUJU)).thenReturn(null);
            when(record.get(GAJI_BATCH_ROOT.NOTES)).thenReturn(null);

            GajiBatchRootResponse response = GajiBatchRootJooqMapper.mapToResponse(record);

            assertNotNull(response, "Response must not be null for ordinal " + expected.ordinal());
            assertEquals(expected, response.status(),
                    "Ordinal " + expected.ordinal() + " must map to " + expected.name());
        }
    }

    @Test
    void mapStatus_nullInteger_returnsNullEnum() {
        Record record = mock(Record.class);
        when(record.get(GAJI_BATCH_ROOT.STATUS)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.ID)).thenReturn("202609-001");
        when(record.get(GAJI_BATCH_ROOT.PERIODE)).thenReturn("202609");
        when(record.get(GAJI_BATCH_ROOT.TOTAL_PEGAWAI)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.TANGGAL_PROSES)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.DI_PROSES_OLEH)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.JABATAN_PEMROSES)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.TANGGAL_VERIFIKASI_TAHAP1)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.DI_VERIFIKASI_OLEH_TAHAP1)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.JABATAN_VERIFIKASI_TAHAP1)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.TANGGAL_VERIFIKASI_TAHAP2)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.DI_VERIFIKASI_OLEH_TAHAP2)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.JABATAN_VERIFIKASI_TAHAP2)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.TANGGAL_PERSETUJUAN)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.DI_SETUJUI_OLEH)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.JABATAN_PENYETUJU)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.NOTES)).thenReturn(null);

        GajiBatchRootResponse response = GajiBatchRootJooqMapper.mapToResponse(record);

        assertNull(response.status(), "Null integer status must map to null enum");
    }

    @Test
    void mapToResponse_nullRecord_returnsNull() {
        assertNull(GajiBatchRootJooqMapper.mapToResponse(null),
                "Null record must return null");
    }

    @Test
    void mapStatus_pendingOrdinal_isZero() {
        Record record = mock(Record.class);
        when(record.get(GAJI_BATCH_ROOT.STATUS)).thenReturn(0);
        when(record.get(GAJI_BATCH_ROOT.ID)).thenReturn("202609-001");
        when(record.get(GAJI_BATCH_ROOT.PERIODE)).thenReturn("202609");
        when(record.get(GAJI_BATCH_ROOT.TOTAL_PEGAWAI)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.TANGGAL_PROSES)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.DI_PROSES_OLEH)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.JABATAN_PEMROSES)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.TANGGAL_VERIFIKASI_TAHAP1)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.DI_VERIFIKASI_OLEH_TAHAP1)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.JABATAN_VERIFIKASI_TAHAP1)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.TANGGAL_VERIFIKASI_TAHAP2)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.DI_VERIFIKASI_OLEH_TAHAP2)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.JABATAN_VERIFIKASI_TAHAP2)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.TANGGAL_PERSETUJUAN)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.DI_SETUJUI_OLEH)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.JABATAN_PENYETUJU)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.NOTES)).thenReturn(null);

        GajiBatchRootResponse response = GajiBatchRootJooqMapper.mapToResponse(record);

        assertEquals(EProsesGaji.PENDING, response.status(),
                "Integer 0 must map to PENDING");
    }

    @Test
    void mapStatus_finishedOrdinal_isFive() {
        Record record = mock(Record.class);
        when(record.get(GAJI_BATCH_ROOT.STATUS)).thenReturn(5);
        when(record.get(GAJI_BATCH_ROOT.ID)).thenReturn("202609-001");
        when(record.get(GAJI_BATCH_ROOT.PERIODE)).thenReturn("202609");
        when(record.get(GAJI_BATCH_ROOT.TOTAL_PEGAWAI)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.TANGGAL_PROSES)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.DI_PROSES_OLEH)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.JABATAN_PEMROSES)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.TANGGAL_VERIFIKASI_TAHAP1)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.DI_VERIFIKASI_OLEH_TAHAP1)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.JABATAN_VERIFIKASI_TAHAP1)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.TANGGAL_VERIFIKASI_TAHAP2)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.DI_VERIFIKASI_OLEH_TAHAP2)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.JABATAN_VERIFIKASI_TAHAP2)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.TANGGAL_PERSETUJUAN)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.DI_SETUJUI_OLEH)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.JABATAN_PENYETUJU)).thenReturn(null);
        when(record.get(GAJI_BATCH_ROOT.NOTES)).thenReturn(null);

        GajiBatchRootResponse response = GajiBatchRootJooqMapper.mapToResponse(record);

        assertEquals(EProsesGaji.FINISHED, response.status(),
                "Integer 5 must map to FINISHED");
    }
}
