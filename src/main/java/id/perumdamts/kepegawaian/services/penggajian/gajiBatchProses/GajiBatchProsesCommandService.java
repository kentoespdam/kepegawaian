package id.perumdamts.kepegawaian.services.penggajian.gajiBatchProses;

import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchRootRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class GajiBatchProsesCommandService {
    private final GajiBatchRootRepository repository;

    @Transactional
    public void prosesGaji(String rootBatchId) {
        GajiBatchRoot batch = repository.findById(rootBatchId)
                .orElseThrow(() -> new NotFoundException("Unknown Batch Process"));
        batch.setStatus(EProsesGaji.PROSES);
        batch.setTanggalProses(LocalDateTime.now());
        repository.save(batch);
        // ponytail: stub Wave 2 — snapshot (W5) + kalkulasi (W6) + reset idempoten
        // diisi Wave 7. Status sengaja berhenti di PROSES sampai engine lengkap.
        log.info("Batch {} marked PROSES; snapshot/kalkulasi engine arrives in Wave 7", rootBatchId);
    }
}
