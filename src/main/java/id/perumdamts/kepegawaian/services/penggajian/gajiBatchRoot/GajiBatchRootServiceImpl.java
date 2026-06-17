package id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootProcessRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisPotonganGaji;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootLampiran;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchRootLampiranRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchRootRepository;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import id.perumdamts.kepegawaian.utils.ProcessPotonganTkk;
import id.perumdamts.kepegawaian.utils.UploadResultUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GajiBatchRootServiceImpl implements GajiBatchRootService {
    @Value("${spring.kafka.topic}")
    private String PENGGAJIAN_TOPIC;
    private final GajiBatchRootRepository repository;
    private final FileUploadUtil fileUploadUtil;
    private final ProcessPotonganTkk processPotonganTkk;
    private final GajiBatchRootLampiranRepository gajiBatchRootLampiranRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public Page<GajiBatchRootResponse> findAll(GajiBatchRootRequest request) {
        if (request.getSortBy() == null) {
            request.setSortBy("id");
            request.setSortDirection("DESC");
        }
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(GajiBatchRootResponse::from);
    }


    @Override
    @Transactional
    public SavedStatus<?> save(GajiBatchRootPostRequest request) {
        try {
            Optional<GajiBatchRoot> byId = repository.findById(request.getPeriode());
            if (byId.isPresent())
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Batch already exists");

            GajiBatchRoot entity = GajiBatchRootPostRequest.toEntityPhase1(request);
            repository.findDeletedBatchRoot(request.getPeriode())
                    .ifPresent(gajiBatchRoot -> {
                        String nextBatchId = request.nextBatchId(gajiBatchRoot.getId());
                        entity.setId(nextBatchId);
                    });

            GajiBatchRoot save = repository.save(entity);

            if (request.getFileName() != null) {
                UploadResultUtil uploadResultUtil = fileUploadUtil.uploadPenggajian(
                        request.getFileName(),
                        "PotonganTKK/" + entity.getPeriode()
                );
                GajiBatchRootLampiran gajiBatchRootLampiran = new GajiBatchRootLampiran(
                        entity,
                        EJenisPotonganGaji.POTONGAN_TKK,
                        uploadResultUtil.getMimeType(),
                        uploadResultUtil.getFileName(),
                        uploadResultUtil.getHashedFileName());
                gajiBatchRootLampiranRepository.save(gajiBatchRootLampiran);
                processPotonganTkk.process(entity.getId());
            }
            final String batchId = save.getId();
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    kafkaTemplate.send(PENGGAJIAN_TOPIC, batchId).whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish batch {} to topic {}", batchId, PENGGAJIAN_TOPIC, ex);
                        } else if (result != null && result.getRecordMetadata() != null) {
                            log.info("Published batch {} to topic {}: offset={}",
                                    batchId, PENGGAJIAN_TOPIC, result.getRecordMetadata().offset());
                        }
                    });
                }
            });
            return SavedStatus.build(ESaveStatus.SUCCESS, "Batch Gaji Saved");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> reprocess(String id, GajiBatchRootProcessRequest request) {
        try {
            GajiBatchRoot entity = repository.findById(request.getId())
                    .orElseThrow(() -> new RuntimeException("Unknown Batch Process"));

            reprocessHandler(entity, request);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Reprocess Penggajian Executed");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> verify1(String id, GajiBatchRootProcessRequest request) {
        try {
            Optional<GajiBatchRoot> byId = repository.findById(request.getId());
            if (byId.isEmpty())
                return SavedStatus.build(ESaveStatus.FAILED, "Unknown Batch Process");
            GajiBatchRoot gajiBatchRoot = GajiBatchRootProcessRequest.verifyPhase1(byId.get(), request);
            repository.save(gajiBatchRoot);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Verifikasi Tahap 1 Saved");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> verify2(String id, GajiBatchRootProcessRequest request) {
        try {
            Optional<GajiBatchRoot> byId = repository.findById(request.getId());
            if (byId.isEmpty())
                return SavedStatus.build(ESaveStatus.FAILED, "Unknown Batch Process");
            GajiBatchRoot gajiBatchRoot = GajiBatchRootProcessRequest.verifyPhase2(byId.get(), request);
            repository.save(gajiBatchRoot);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Verifikasi Tahap 2 Saved");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> accept(String id, GajiBatchRootProcessRequest request) {
        try {
            Optional<GajiBatchRoot> byId = repository.findById(request.getId());
            if (byId.isEmpty())
                return SavedStatus.build(ESaveStatus.FAILED, "Unknown Batch Process");
            GajiBatchRoot gajiBatchRoot = GajiBatchRootProcessRequest.accept(byId.get(), request);
            repository.save(gajiBatchRoot);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Batch Accepted");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public boolean delete(String id) {
        Optional<GajiBatchRoot> byId = repository.findById(id);
        if (byId.isEmpty()
                || byId.get().getStatus().equals(EProsesGaji.PROSES.value())
                || byId.get().getStatus().equals(EProsesGaji.FINISHED.value()))
            return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }

    private void reprocessHandler(GajiBatchRoot entity, GajiBatchRootProcessRequest request) {
        switch (request.getPhase()) {
            case WAIT_APPROVAL -> entity.setStatus(EProsesGaji.WAIT_VERIFICATION_PHASE_2.ordinal());
            case WAIT_VERIFICATION_PHASE_2 -> entity.setStatus(EProsesGaji.WAIT_VERIFICATION_PHASE_1.ordinal());
            case WAIT_VERIFICATION_PHASE_1 -> entity.setStatus(EProsesGaji.PENDING.ordinal());
        }
        entity.setTanggalVerifikasiTahap1(LocalDateTime.now());
        entity.setDiVerifikasiOlehTahap1(request.getNama());
        entity.setJabatanVerifikasiTahap1(request.getJabatan());
        GajiBatchRoot save = repository.save(entity);
        if (save.getStatus().equals(EProsesGaji.PENDING.ordinal())) {
            kafkaTemplate.send(PENGGAJIAN_TOPIC, entity.getId());
        }
    }
}
