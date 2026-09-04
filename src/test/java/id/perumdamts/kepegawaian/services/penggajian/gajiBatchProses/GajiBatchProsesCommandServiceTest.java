package id.perumdamts.kepegawaian.services.penggajian.gajiBatchProses;

import id.perumdamts.kepegawaian.entities.commons.EJenisErrorGaji;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootErrorLogs;
import id.perumdamts.kepegawaian.exceptions.GajiFormulaException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchMasterProsesRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchMasterRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchRootRepository;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.GajiBatchProsesKalkulasiService;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.GajiBatchProsesSnapshotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GajiBatchProsesCommandServiceTest {
    private static final String BATCH_ID = "202609-001";

    @Mock GajiBatchRootRepository repository;
    @Mock GajiBatchMasterRepository gajiBatchMasterRepository;
    @Mock GajiBatchMasterProsesRepository gajiBatchMasterProsesRepository;
    @Mock GajiBatchProsesSnapshotService snapshotService;
    @Mock GajiBatchProsesKalkulasiService kalkulasiService;

    private GajiBatchProsesCommandService service;

    @BeforeEach
    void setUp() {
        service = new GajiBatchProsesCommandService(
                repository, gajiBatchMasterRepository, gajiBatchMasterProsesRepository,
                snapshotService, kalkulasiService);
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
        when(repository.findById(BATCH_ID)).thenReturn(Optional.of(entity));
        when(gajiBatchMasterRepository.findByGajiBatchRoot_Id(BATCH_ID)).thenReturn(List.of());
        when(snapshotService.snapshot(entity)).thenReturn(List.of(
                master(1L, "111", "A"), master(2L, "222", "B")));

        service.prosesGaji(BATCH_ID);

        verify(kalkulasiService, times(2)).hitung(any(GajiBatchMaster.class), eq(BATCH_ID));
        assertEquals(EProsesGaji.WAIT_VERIFICATION_PHASE_1, entity.getStatus());
        assertEquals(2, entity.getTotalPegawai());
        assertTrue(entity.getErrorLogs().isEmpty());
        verify(repository, times(2)).save(entity);
    }

    @Test
    void prosesGaji_errorPerPegawai_dataErrorDicatatLanjut() {
        GajiBatchRoot entity = batch(EProsesGaji.PENDING);
        when(repository.findById(BATCH_ID)).thenReturn(Optional.of(entity));
        when(gajiBatchMasterRepository.findByGajiBatchRoot_Id(BATCH_ID)).thenReturn(List.of());
        when(snapshotService.snapshot(entity)).thenReturn(List.of(
                master(1L, "111", "A"), master(2L, "222", "B")));
        // pegawai pertama gagal (formula) — pegawai kedua tetap dihitung
        doThrow(new GajiFormulaException("GP + X", new IllegalStateException("var X")))
                .doNothing()
                .when(kalkulasiService).hitung(any(GajiBatchMaster.class), anyString());

        service.prosesGaji(BATCH_ID);

        // error per pegawai tidak menggagalkan batch
        assertEquals(EProsesGaji.WAIT_VERIFICATION_PHASE_1, entity.getStatus());
        assertEquals(2, entity.getTotalPegawai());
        assertEquals(1, entity.getErrorLogs().size());
        GajiBatchRootErrorLogs error = entity.getErrorLogs().iterator().next();
        assertEquals(EJenisErrorGaji.DATA, error.getJenisError());
        assertEquals("111", error.getNipam());
        assertTrue(error.getNotes().contains("Formula tidak valid"));
    }

    @Test
    void prosesGaji_errorPerPegawai_systemErrorDicatatLanjut() {
        GajiBatchRoot entity = batch(EProsesGaji.PENDING);
        when(repository.findById(BATCH_ID)).thenReturn(Optional.of(entity));
        when(gajiBatchMasterRepository.findByGajiBatchRoot_Id(BATCH_ID)).thenReturn(List.of());
        when(snapshotService.snapshot(entity)).thenReturn(List.of(master(1L, "111", "A")));
        doThrow(new IllegalStateException("db down"))
                .when(kalkulasiService).hitung(any(GajiBatchMaster.class), anyString());

        service.prosesGaji(BATCH_ID);

        assertEquals(EProsesGaji.WAIT_VERIFICATION_PHASE_1, entity.getStatus());
        GajiBatchRootErrorLogs error = entity.getErrorLogs().iterator().next();
        assertEquals(EJenisErrorGaji.SYSTEM, error.getJenisError());
        assertEquals("db down", error.getNotes());
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