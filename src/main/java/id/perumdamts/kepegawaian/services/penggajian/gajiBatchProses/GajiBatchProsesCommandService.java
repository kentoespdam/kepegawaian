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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

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
 * <p><b>Transaksi PER FASE</b> (bukan satu transaksi besar): snapshot harus COMMIT dulu
 * sebelum fork kalkulasi jalan — fork membuka transaksi sendiri (koneksi berbeda) sehingga
 * tidak bisa melihat master yang belum commit (InnoDB isolation); satu transaksi besar
 * membuat semua fork gagal "Master hilang"/"Row already updated or deleted".
 *
 * <p>Alur: PROSES (tx) → reset hasil lama (tx) → snapshot (tx, commit) → kalkulasi paralel
 * per pegawai (fork, tx masing-masing; error per pegawai dicatat & dilanjutkan) →
 * simpan hasil (tx). Fatal → FAILED + error SYSTEM (tx terpisah, tidak rethrow).
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
    private final TransactionTemplate tx;

    /** Error per pegawai — dikumpulkan dari fork, dicatat di thread utama (session Hibernate tidak thread-safe). */
    private record ErrorEntry(String nipam, String nama, EJenisErrorGaji jenis, String notes) {
    }

    public void prosesGaji(String rootBatchId) {
        tx.executeWithoutResult(s -> {
            GajiBatchRoot b = root(rootBatchId);
            b.setStatus(EProsesGaji.PROSES);
            b.setTanggalProses(LocalDateTime.now());
            repository.save(b);
        });

        try {
            reset(rootBatchId);
            GajiBatchRoot batch = root(rootBatchId); // detached — hanya utk batchId/periode
            List<GajiBatchMaster> masters = snapshotService.snapshot(batch);
            List<ErrorEntry> errors = prosesParallel(batch.getId(), masters);
            tx.executeWithoutResult(s -> {
                GajiBatchRoot b = root(rootBatchId);
                errors.forEach(e -> catatError(b, e));
                b.setTotalPegawai(masters.size());
                b.setStatus(EProsesGaji.WAIT_VERIFICATION_PHASE_1);
                repository.save(b);
            });
        } catch (Exception fatal) {
            log.error("Fatal error memproses batch {}", rootBatchId, fatal);
            tx.executeWithoutResult(s -> {
                GajiBatchRoot b = root(rootBatchId);
                b.setStatus(EProsesGaji.FAILED);
                catatError(b, new ErrorEntry(null, null, EJenisErrorGaji.SYSTEM, "Fatal: " + fatal.getMessage()));
                repository.save(b);
            });
            // tidak rethrow — FAILED + error log ter-persist oleh transaksi di atas
        }
    }

    /**
     * Hapus hasil proses lama: {@code GajiBatchMasterProses} (cascade manual via
     * batchMasterId) lalu {@code GajiBatchMaster} batch — engine idempoten (W7-1 step 2).
     *
     * <p>Pakai {@link TransactionTemplate} internal (bukan {@code @Transactional}): dipanggil
     * dari {@link #prosesGaji} (self-invocation — anotasi @Transactional tidak ter-proxy).
     */
    public void reset(String rootBatchId) {
        tx.executeWithoutResult(s -> {
            List<GajiBatchMaster> masters = gajiBatchMasterRepository.findByGajiBatchRoot_Id(rootBatchId);
            List<Long> masterIds = masters.stream().map(GajiBatchMaster::getId).toList();
            if (!masterIds.isEmpty())
                gajiBatchMasterProsesRepository.deleteByBatchMasterIdIn(masterIds);
            gajiBatchMasterRepository.deleteAll(masters);
            log.info("Reset batch {}: {} master dihapus", rootBatchId, masters.size());
        });
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
    private List<ErrorEntry> prosesParallel(String batchId, List<GajiBatchMaster> masters) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<ErrorEntry>> futures = masters.stream()
                    .map(master -> executor.submit(() -> hitungSatu(batchId, master)))
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

    private ErrorEntry hitungSatu(String batchId, GajiBatchMaster master) {
        try {
            // re-fetch instance FRESH per fork dalam transaksi fork sendiri: master hasil
            // snapshot ter-manage transaksi lain → entitas JPA tidak boleh dibagikan antar thread.
            GajiBatchMaster fresh = gajiBatchMasterRepository.findById(master.getId())
                    .orElseThrow(() -> new IllegalStateException("Master " + master.getId() + " hilang"));
            kalkulasiService.hitung(fresh, batchId);
            return null;
        } catch (GajiFormulaException e) {
            // formula/seed bermasalah → DATA error (per pegawai, lanjut)
            log.warn("DATA error pegawai {}: {}", master.getNipam(), e.getMessage());
            return new ErrorEntry(master.getNipam(), master.getNama(), EJenisErrorGaji.DATA, e.getMessage());
        } catch (Exception e) {
            log.error("SYSTEM error pegawai {}: {}", master.getNipam(), e.getMessage(), e);
            return new ErrorEntry(master.getNipam(), master.getNama(), EJenisErrorGaji.SYSTEM, e.getMessage());
        }
    }

    private GajiBatchRoot root(String rootBatchId) {
        return repository.findById(rootBatchId)
                .orElseThrow(() -> new NotFoundException("Unknown Batch Process"));
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