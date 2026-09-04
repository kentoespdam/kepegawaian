package id.perumdamts.kepegawaian.services.penggajian.gajiBatchProses;

import id.perumdamts.kepegawaian.entities.commons.EJenisErrorGaji;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootErrorLogs;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchRootRepository;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot.GajiBatchRootEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Wave 8 — recovery saat startup (lihat docs/penggajian-proses-gaji-claim-order.md):
 * <ul>
 *   <li>Batch tertinggal {@code PROSES} (server restart saat proses berjalan) → {@code FAILED}
 *       + error log SYSTEM "Server restart detected".</li>
 *   <li>Batch {@code PENDING} yang belum sempat diproses → publish ulang
 *       {@code GajiBatchRootProcessEvent} (re-queue).</li>
 * </ul>
 *
 * <p>{@code onApplicationEvent} di-{@code @Transactional} agar publish PENDING benar-benar
 * diteruskan: listener {@code GajiBatchRootEventListener} adalah
 * {@code @TransactionalEventListener(AFTER_COMMIT)} — tanpa transaksi aktif saat publish,
 * eventnya dibuang (default fallbackExecution=false).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GajiBatchProsesStartupService implements ApplicationListener<ApplicationReadyEvent> {

    private final GajiBatchRootRepository repository;
    private final GajiBatchRootEventPublisher eventPublisher;

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        recoverProses();
        requeuePending();
    }

    /** Batch PROSES saat startup → FAILED + error SYSTEM (proses terputus oleh restart). */
    @Transactional
    public void recoverProses() {
        List<GajiBatchRoot> tertinggal = repository.findByStatus(EProsesGaji.PROSES);
        for (GajiBatchRoot batch : tertinggal) {
            log.warn("Server restart detected — batch {} berstatus PROSES di-set FAILED", batch.getId());
            batch.setStatus(EProsesGaji.FAILED);
            GajiBatchRootErrorLogs error = new GajiBatchRootErrorLogs();
            error.setGajiBatchRoot(batch);
            error.setJenisError(EJenisErrorGaji.SYSTEM);
            error.setNotes("Server restart detected — proses terputus");
            batch.getErrorLogs().add(error);
        }
        if (!tertinggal.isEmpty())
            repository.saveAll(tertinggal);
    }

    /** Batch PENDING yang belum diproses → publish ulang event proses gaji (re-queue). */
    @Transactional
    public void requeuePending() {
        List<GajiBatchRoot> pending = repository.findByStatus(EProsesGaji.PENDING);
        for (GajiBatchRoot batch : pending) {
            log.info("Re-queue batch {} (PENDING saat startup)", batch.getId());
            eventPublisher.publishAfterCommit(batch.getId());
        }
    }
}