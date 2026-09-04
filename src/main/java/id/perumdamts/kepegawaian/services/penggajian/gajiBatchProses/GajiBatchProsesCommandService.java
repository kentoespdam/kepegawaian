package id.perumdamts.kepegawaian.services.penggajian.gajiBatchProses;

import id.perumdamts.kepegawaian.entities.commons.EJenisErrorGaji;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootErrorLogs;
import id.perumdamts.kepegawaian.exceptions.GajiFormulaException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchMasterProsesRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchMasterRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchRootRepository;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.GajiBatchProsesKalkulasiService;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.GajiBatchProsesSnapshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Orchestrator proses gaji (Wave 7) — lihat docs/penggajian-proses-gaji-claim-order.md.
 *
 * <p>Dipanggil dari {@code GajiBatchRootEventListener} (after-commit, virtual thread
 * {@code gajiProsesExecutor}). Alur: status PROSES → reset hasil lama → snapshot
 * eligible pegawai → kalkulasi paralel per pegawai (StructuredTaskScope, error per
 * pegawai dicatat & dilanjutkan) → WAIT_VERIFICATION_PHASE_1. Fatal → FAILED + error
 * SYSTEM (tidak rethrow: status FAILED harus ter-persist, bukan rollback).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GajiBatchProsesCommandService {

    private final GajiBatchRootRepository repository;
    private final GajiBatchMasterRepository gajiBatchMasterRepository;
    private final GajiBatchMasterProsesRepository gajiBatchMasterProsesRepository;
    private final GajiBatchProsesSnapshotService snapshotService;
    private final GajiBatchProsesKalkulasiService kalkulasiService;

    /** Error per pegawai — dikumpulkan dari fork, dicatat di thread utama (session Hibernate tidak thread-safe). */
    private record ErrorEntry(String nipam, String nama, EJenisErrorGaji jenis, String notes) {
    }

    @Transactional
    public void prosesGaji(String rootBatchId) {
        GajiBatchRoot batch = repository.findById(rootBatchId)
                .orElseThrow(() -> new NotFoundException("Unknown Batch Process"));
        batch.setStatus(EProsesGaji.PROSES);
        batch.setTanggalProses(LocalDateTime.now());
        repository.save(batch);

        try {
            reset(rootBatchId);
            List<GajiBatchMaster> masters = snapshotService.snapshot(batch);
            List<ErrorEntry> errors = prosesParallel(batch, masters);
            errors.forEach(e -> catatError(batch, e));
            batch.setTotalPegawai(masters.size());
            batch.setStatus(EProsesGaji.WAIT_VERIFICATION_PHASE_1);
        } catch (Exception fatal) {
            log.error("Fatal error memproses batch {}", rootBatchId, fatal);
            batch.setStatus(EProsesGaji.FAILED);
            catatError(batch, new ErrorEntry(null, null, EJenisErrorGaji.SYSTEM, "Fatal: " + fatal.getMessage()));
            // tidak rethrow — FAILED + error log ter-persist oleh commit normal
        }
        repository.save(batch);
    }

    /**
     * Hapus hasil proses lama: {@code GajiBatchMasterProses} (cascade manual via
     * batchMasterId) lalu {@code GajiBatchMaster} batch — engine idempoten (W7-1 step 2).
     */
    @Transactional
    public void reset(String rootBatchId) {
        List<GajiBatchMaster> masters = gajiBatchMasterRepository.findByGajiBatchRoot_Id(rootBatchId);
        List<Long> masterIds = masters.stream().map(GajiBatchMaster::getId).toList();
        if (!masterIds.isEmpty())
            gajiBatchMasterProsesRepository.deleteByBatchMasterIdIn(masterIds);
        gajiBatchMasterRepository.deleteAll(masters);
        log.info("Reset batch {}: {} master dihapus", rootBatchId, masters.size());
    }

    /**
     * Kalkulasi paralel per pegawai — error per pegawai tidak menggagalkan batch.
     *
     * ponytail: spec W7-1 minta StructuredTaskScope.ShutdownOnFailure, tapi API itu masih
     * preview di JDK 25 (butuh --enable-preview di compile+test+run). Padanan setara:
     * virtual-thread executor + submit/get — hitungSatu sudah men-catch SEMUA exception
     * (kembali ErrorEntry/null), jadi tak ada subtask yg gagal → wait-all identik.
     * Ganti ke StructuredTaskScope bila project mengaktifkan --enable-preview.
     */
    private List<ErrorEntry> prosesParallel(GajiBatchRoot batch, List<GajiBatchMaster> masters) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<ErrorEntry>> futures = masters.stream()
                    .map(master -> executor.submit(() -> hitungSatu(batch, master)))
                    .toList();
            List<ErrorEntry> errors = new ArrayList<>();
            for (Future<ErrorEntry> f : futures)
                errors.add(f.get()); // hitungSatu tidak pernah throw — aman
            return errors.stream().filter(Objects::nonNull).toList();
        } catch (InterruptedException | ExecutionException e) {
            // Interrupted → propagasi; ExecutionException tak mungkin (hitungSatu tak pernah throw)
            if (e instanceof InterruptedException ie)
                Thread.currentThread().interrupt();
            throw new RuntimeException("Proses gaji paralel gagal", e);
        }
    }

    private ErrorEntry hitungSatu(GajiBatchRoot batch, GajiBatchMaster master) {
        try {
            kalkulasiService.hitung(master, batch.getId());
            return null;
        } catch (GajiFormulaException e) {
            // formula/seed bermasalah → DATA error (per pegawai, lanjut)
            return new ErrorEntry(master.getNipam(), master.getNama(), EJenisErrorGaji.DATA, e.getMessage());
        } catch (Exception e) {
            return new ErrorEntry(master.getNipam(), master.getNama(), EJenisErrorGaji.SYSTEM, e.getMessage());
        }
    }

    private void catatError(GajiBatchRoot batch, ErrorEntry e) {
        GajiBatchRootErrorLogs error = new GajiBatchRootErrorLogs();
        error.setGajiBatchRoot(batch);
        error.setNipam(e.nipam());
        error.setNama(e.nama());
        error.setJenisError(e.jenis());
        error.setNotes(e.notes());
        batch.getErrorLogs().add(error); // cascade ALL → persist saat save root
    }
}