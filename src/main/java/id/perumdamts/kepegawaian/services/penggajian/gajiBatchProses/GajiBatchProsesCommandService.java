package id.perumdamts.kepegawaian.services.penggajian.gajiBatchProses;

import id.perumdamts.kepegawaian.entities.commons.EJenisErrorGaji;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Orchestrator proses gaji (Wave 7) — dioptimasi dengan preload pattern dan batch insert.
 *
 * <p>Alur:
 * 1) PROSES (tx)
 * 2) Reset hasil lama (tx)
 * 3) Snapshot (tx, commit)
 * 4) Preload context (Redis cache + batch live query)
 * 5) Kalkulasi paralel (zero-DB virtual threads)
 * 6) Bulk save (tx: JdbcTemplate batchInsert proses + saveAll masters + summary notes)
 *
 * <p>Fatal error → FAILED + error SYSTEM.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GajiBatchProsesCommandService {

    private final GajiBatchRootRepository repository;
    private final GajiBatchMasterRepository gajiBatchMasterRepository;
    private final GajiBatchMasterProsesRepository gajiBatchMasterProsesRepository;
    private final GajiBatchMasterProsesJdbcRepository gajiBatchMasterProsesJdbcRepository;
    private final GajiBatchProsesSnapshotService snapshotService;
    private final GajiPreloadService preloadService;
    private final GajiBatchProsesKalkulasiService kalkulasiService;
    private final TransactionTemplate tx;

    public void prosesGaji(String rootBatchId) {
        StopWatch watch = new StopWatch("Batch " + rootBatchId);
        long startTime = System.currentTimeMillis();

        tx.executeWithoutResult(s -> {
            GajiBatchRoot b = root(rootBatchId);
            b.setStatus(EProsesGaji.PROSES);
            b.setTanggalProses(LocalDateTime.now());
            repository.save(b);
        });

        try {
            reset(rootBatchId);
            GajiBatchRoot batch = root(rootBatchId); // detached — hanya utk batchId/periode

            watch.start("snapshot");
            List<GajiBatchMaster> masters = snapshotService.snapshot(batch);
            watch.stop();

            watch.start("preload");
            GajiPreloadContext ctx = preloadService.preload(batch.getId(), batch.getPeriode(), masters);
            watch.stop();

            watch.start("kalkulasi");
            List<HitungPegawaiResult> results = prosesParallel(masters, ctx);
            watch.stop();

            watch.start("bulk-save");
            List<HitungPegawaiResult> sukses = results.stream().filter(HitungPegawaiResult::isSuccess).toList();
            List<HitungPegawaiResult> gagal = results.stream().filter(r -> !r.isSuccess()).toList();

            tx.executeWithoutResult(s -> {
                GajiBatchRoot b = root(rootBatchId);

                // 1) Bulk insert proses list
                List<GajiBatchMasterProses> allProses = sukses.stream()
                        .flatMap(r -> r.prosesList().stream())
                        .toList();
                gajiBatchMasterProsesJdbcRepository.batchInsert(allProses);

                // 2) Bulk update master
                List<GajiBatchMaster> mastersToUpdate = sukses.stream()
                        .map(HitungPegawaiResult::master)
                        .toList();
                gajiBatchMasterRepository.saveAll(mastersToUpdate);

                // 3) Catat error logs jika ada gagal
                gagal.forEach(r -> catatError(b, r.error()));

                // 4) Set notes JSON di batch root
                double durasiDetik = (System.currentTimeMillis() - startTime) / 1000.0;
                String notes = String.format(Locale.US,
                        "{\"totalPegawai\":%d,\"berhasil\":%d,\"gagal\":%d,\"durasiDetik\":%.2f}",
                        masters.size(), sukses.size(), gagal.size(), durasiDetik);
                b.setNotes(notes);
                b.setTotalPegawai(masters.size());

                // 5) Set status: jika ada gagal -> FAILED, jika semua berhasil -> WAIT_VERIFICATION_PHASE_1
                b.setStatus(gagal.isEmpty() ? EProsesGaji.WAIT_VERIFICATION_PHASE_1 : EProsesGaji.FAILED);
                repository.save(b);
            });
            watch.stop();

            log.info("[GAJI] Batch {} selesai: {}", rootBatchId, watch.prettyPrint());
        } catch (Exception fatal) {
            if (watch.isRunning()) {
                watch.stop();
            }
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
     * Kalkulasi paralel per pegawai menggunakan virtual threads dengan zero-DB access.
     */
    private List<HitungPegawaiResult> prosesParallel(List<GajiBatchMaster> masters, GajiPreloadContext ctx) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<HitungPegawaiResult>> futures = masters.stream()
                    .map(master -> executor.submit(() -> hitungSatu(master, ctx)))
                    .toList();
            List<HitungPegawaiResult> results = new ArrayList<>();
            for (Future<HitungPegawaiResult> f : futures) {
                results.add(f.get());
            }
            return results;
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException)
                Thread.currentThread().interrupt();
            throw new RuntimeException("Proses gaji paralel gagal", e);
        }
    }

    private HitungPegawaiResult hitungSatu(GajiBatchMaster master, GajiPreloadContext ctx) {
        return kalkulasiService.hitung(master, ctx);
    }

    private GajiBatchRoot root(String rootBatchId) {
        return repository.findById(rootBatchId)
                .orElseThrow(() -> new NotFoundException("Unknown Batch Process"));
    }

    private void catatError(GajiBatchRoot batch, ErrorEntry e) {
        if (e == null) return;
        GajiBatchRootErrorLogs error = new GajiBatchRootErrorLogs();
        error.setGajiBatchRoot(batch);
        error.setNipam(e.nipam());
        error.setNama(e.nama());
        error.setJenisError(e.jenis());
        error.setNotes(e.notes());
        batch.getErrorLogs().add(error); // cascade ALL → persist saat save root
    }
}