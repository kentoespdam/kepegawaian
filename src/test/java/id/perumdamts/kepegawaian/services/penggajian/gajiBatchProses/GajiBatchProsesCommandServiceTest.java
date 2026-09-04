package id.perumdamts.kepegawaian.services.penggajian.gajiBatchProses;

import id.perumdamts.kepegawaian.entities.commons.EJenisErrorGaji;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootErrorLogs;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.penggajian.jdbc.GajiBatchMasterProsesJdbcRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchMasterProsesRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchMasterRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchRootRepository;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.GajiBatchProsesKalkulasiService;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.GajiBatchProsesSnapshotService;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.preload.ErrorEntry;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.preload.GajiPreloadContext;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.preload.GajiPreloadService;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.preload.HitungPegawaiResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GajiBatchProsesCommandServiceTest {
    private static final String BATCH_ID = "202609-001";

    @Mock GajiBatchRootRepository repository;
    @Mock GajiBatchMasterRepository gajiBatchMasterRepository;
    @Mock GajiBatchMasterProsesRepository gajiBatchMasterProsesRepository;
    @Mock GajiBatchMasterProsesJdbcRepository gajiBatchMasterProsesJdbcRepository;
    @Mock GajiBatchProsesSnapshotService snapshotService;
    @Mock GajiPreloadService preloadService;
    @Mock GajiBatchProsesKalkulasiService kalkulasiService;

    private GajiBatchProsesCommandService service;

    @BeforeEach
    void setUp() {
        service = new GajiBatchProsesCommandService(
                repository, gajiBatchMasterRepository, gajiBatchMasterProsesRepository,
                gajiBatchMasterProsesJdbcRepository, snapshotService, preloadService,
                kalkulasiService, stubTransactionTemplate());
    }

    /** ResourcelessTransactionManager dihapus di Spring 7 — pakai stub inline. */
    private static TransactionTemplate stubTransactionTemplate() {
        return new TransactionTemplate(new PlatformTransactionManager() {
            @Override
            public TransactionStatus getTransaction(TransactionDefinition definition) {
                return new SimpleTransactionStatus();
            }

            @Override
            public void commit(TransactionStatus status) {
            }

            @Override
            public void rollback(TransactionStatus status) {
            }
        });
    }

    private GajiBatchRoot batch(EProsesGaji status) {
        GajiBatchRoot entity = new GajiBatchRoot();
        entity.setId(BATCH_ID);
        entity.setPeriode("202609");
        entity.setStatus(status);
        return entity;
    }

    private GajiBatchMaster master(long id, String nipam, String nama) {
        GajiBatchMaster m = new GajiBatchMaster();
        m.setId(id);
        m.setNipam(nipam);
        m.setNama(nama);
        return m;
    }

    @Test
    void prosesGaji_success_snapshotDihitungParalel() {
        GajiBatchRoot entity = batch(EProsesGaji.PENDING);
        GajiBatchMaster m1 = master(1L, "111", "A");
        GajiBatchMaster m2 = master(2L, "222", "B");
        when(repository.findById(BATCH_ID)).thenReturn(Optional.of(entity));
        when(gajiBatchMasterRepository.findByGajiBatchRoot_Id(BATCH_ID)).thenReturn(List.of());
        when(snapshotService.snapshot(entity)).thenReturn(List.of(m1, m2));

        GajiPreloadContext ctx = mock(GajiPreloadContext.class);
        when(preloadService.preload(eq(BATCH_ID), eq("202609"), any())).thenReturn(ctx);
        when(kalkulasiService.hitung(eq(m1), eq(ctx))).thenReturn(new HitungPegawaiResult(m1, List.of(), null));
        when(kalkulasiService.hitung(eq(m2), eq(ctx))).thenReturn(new HitungPegawaiResult(m2, List.of(), null));

        service.prosesGaji(BATCH_ID);

        verify(kalkulasiService, times(2)).hitung(any(GajiBatchMaster.class), eq(ctx));
        verify(gajiBatchMasterProsesJdbcRepository).batchInsert(anyList());
        verify(gajiBatchMasterRepository).saveAll(List.of(m1, m2));
        assertEquals(EProsesGaji.WAIT_VERIFICATION_PHASE_1, entity.getStatus());
        assertEquals(2, entity.getTotalPegawai());
        assertTrue(entity.getErrorLogs().isEmpty());
        assertNotNull(entity.getNotes());
        assertTrue(entity.getNotes().contains("\"totalPegawai\":2"));
        assertTrue(entity.getNotes().contains("\"berhasil\":2"));
        assertTrue(entity.getNotes().contains("\"gagal\":0"));
        verify(repository, times(2)).save(entity);
    }

    @Test
    void prosesGaji_errorPerPegawai_dataErrorDicatatLanjut() {
        GajiBatchRoot entity = batch(EProsesGaji.PENDING);
        GajiBatchMaster m1 = master(1L, "111", "A");
        GajiBatchMaster m2 = master(2L, "222", "B");
        when(repository.findById(BATCH_ID)).thenReturn(Optional.of(entity));
        when(gajiBatchMasterRepository.findByGajiBatchRoot_Id(BATCH_ID)).thenReturn(List.of());
        when(snapshotService.snapshot(entity)).thenReturn(List.of(m1, m2));

        GajiPreloadContext ctx = mock(GajiPreloadContext.class);
        when(preloadService.preload(eq(BATCH_ID), eq("202609"), any())).thenReturn(ctx);
        when(kalkulasiService.hitung(eq(m1), eq(ctx)))
                .thenReturn(new HitungPegawaiResult(m1, List.of(), new ErrorEntry("111", "A", EJenisErrorGaji.DATA, "Formula tidak valid")));
        when(kalkulasiService.hitung(eq(m2), eq(ctx)))
                .thenReturn(new HitungPegawaiResult(m2, List.of(), new ErrorEntry("222", "B", EJenisErrorGaji.DATA, "Formula tidak valid")));

        service.prosesGaji(BATCH_ID);

        // error per pegawai menyebabkan status FAILED
        assertEquals(EProsesGaji.FAILED, entity.getStatus());
        assertEquals(2, entity.getTotalPegawai());
        assertEquals(2, entity.getErrorLogs().size());
        for (GajiBatchRootErrorLogs error : entity.getErrorLogs()) {
            assertEquals(EJenisErrorGaji.DATA, error.getJenisError());
            assertTrue(error.getNotes().contains("Formula tidak valid"));
        }
        assertEquals(Set.of("111", "222"), entity.getErrorLogs().stream()
                .map(GajiBatchRootErrorLogs::getNipam).collect(Collectors.toSet()));
        assertNotNull(entity.getNotes());
        assertTrue(entity.getNotes().contains("\"gagal\":2"));
    }

    @Test
    void prosesGaji_errorPerPegawai_systemErrorDicatatLanjut() {
        GajiBatchRoot entity = batch(EProsesGaji.PENDING);
        GajiBatchMaster m1 = master(1L, "111", "A");
        when(repository.findById(BATCH_ID)).thenReturn(Optional.of(entity));
        when(gajiBatchMasterRepository.findByGajiBatchRoot_Id(BATCH_ID)).thenReturn(List.of());
        when(snapshotService.snapshot(entity)).thenReturn(List.of(m1));

        GajiPreloadContext ctx = mock(GajiPreloadContext.class);
        when(preloadService.preload(eq(BATCH_ID), eq("202609"), any())).thenReturn(ctx);
        when(kalkulasiService.hitung(eq(m1), eq(ctx)))
                .thenReturn(new HitungPegawaiResult(m1, List.of(), new ErrorEntry("111", "A", EJenisErrorGaji.SYSTEM, "db down")));

        service.prosesGaji(BATCH_ID);

        assertEquals(EProsesGaji.FAILED, entity.getStatus());
        GajiBatchRootErrorLogs error = entity.getErrorLogs().iterator().next();
        assertEquals(EJenisErrorGaji.SYSTEM, error.getJenisError());
        assertEquals("db down", error.getNotes());
        assertNotNull(entity.getNotes());
        assertTrue(entity.getNotes().contains("\"gagal\":1"));
    }

    @Test
    void prosesGaji_fatal_snapshotGagal_statusFailed() {
        GajiBatchRoot entity = batch(EProsesGaji.PENDING);
        when(repository.findById(BATCH_ID)).thenReturn(Optional.of(entity));
        when(gajiBatchMasterRepository.findByGajiBatchRoot_Id(BATCH_ID)).thenReturn(List.of());
        when(snapshotService.snapshot(entity)).thenThrow(new IllegalStateException("snapshot boom"));

        service.prosesGaji(BATCH_ID);

        assertEquals(EProsesGaji.FAILED, entity.getStatus());
        assertEquals(1, entity.getErrorLogs().size());
        GajiBatchRootErrorLogs error = entity.getErrorLogs().iterator().next();
        assertEquals(EJenisErrorGaji.SYSTEM, error.getJenisError());
        assertTrue(error.getNotes().contains("snapshot boom"));
        // status FAILED + error log ter-persist (commit normal, bukan rollback)
        verify(repository, times(2)).save(entity);
    }

    @Test
    void prosesGaji_reset_menghapusHasilProsesLama() {
        GajiBatchRoot entity = batch(EProsesGaji.PENDING);
        GajiBatchMaster oldMaster = master(10L, "old", "Old");
        when(repository.findById(BATCH_ID)).thenReturn(Optional.of(entity));
        when(gajiBatchMasterRepository.findByGajiBatchRoot_Id(BATCH_ID)).thenReturn(List.of(oldMaster));
        when(snapshotService.snapshot(entity)).thenReturn(List.of());
        when(preloadService.preload(any(), any(), any())).thenReturn(mock(GajiPreloadContext.class));

        service.prosesGaji(BATCH_ID);

        verify(gajiBatchMasterProsesRepository).deleteByBatchMasterIdIn(List.of(10L));
        verify(gajiBatchMasterRepository).deleteAll(List.of(oldMaster));
        assertEquals(EProsesGaji.WAIT_VERIFICATION_PHASE_1, entity.getStatus());
        assertEquals(0, entity.getTotalPegawai());
    }

    @Test
    void prosesGaji_throwsNotFound_whenBatchMissing() {
        when(repository.findById(BATCH_ID)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.prosesGaji(BATCH_ID));

        assertTrue(ex.getMessage().contains("Unknown Batch Process"),
                "Message must mention 'Unknown Batch Process', got: " + ex.getMessage());
        verify(repository, never()).save(any());
    }
}