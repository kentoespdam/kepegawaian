package id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootProcessRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisPotonganGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootLampiran;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchRootLampiranRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchRootRepository;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import id.perumdamts.kepegawaian.utils.ProcessPotonganTkk;
import id.perumdamts.kepegawaian.utils.UploadResultUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static id.perumdamts.kepegawaian.config.KafkaConfig.PENGGAJIAN_TOPIC;

@Service
@RequiredArgsConstructor
public class GajiBatchRootServiceImpl implements GajiBatchRootService {
    private final GajiBatchRootRepository repository;
//    private final GajiBatchRootErrorLogsRepository errorLogsRepository;
    private final FileUploadUtil fileUploadUtil;
    private final ProcessPotonganTkk processPotonganTkk;
    private final GajiBatchRootLampiranRepository gajiBatchRootLampiranRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    @Transactional
    public Page<GajiBatchRootResponse> findAll(GajiBatchRootRequest request) {
        if (request.getSortBy() == null) {
            request.setSortBy("id");
            request.setSortDirection("DESC");
        }
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(GajiBatchRootResponse::from);

//        return repository.findAll(request.getSpecification(), request.getPageable())
//                .map(batchRoot -> {
//                    List<GajiBatchRootErrorLogs> errorLogs = errorLogsRepository.findByGajiBatchRoot_Id(batchRoot.getId());
//                    return GajiBatchRootResponse.from(batchRoot, errorLogs);
//                });
    }


//    @Transactional
//    @Override
//    public List<GajiBatchRootErrorLogsResponse> findErrorLogs(String id) {
//        return errorLogsRepository.findByGajiBatchRoot_Id(id)
//                .stream()
//                .map(GajiBatchRootErrorLogsResponse::from)
//                .toList();
//    }

    @Override
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

            if (request.getFile() != null) {
                UploadResultUtil uploadResultUtil = fileUploadUtil.uploadPenggajian(
                        request.getFile(),
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
//            kafkaTemplate.send(PENGGAJIAN_TOPIC, mapper.writeValueAsString(save.getId()));
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> reprocess(String id, GajiBatchRootProcessRequest request) {
        try {
            Optional<GajiBatchRoot> optionalBatchRoot = repository.findById(request.getBatchId());
            if (optionalBatchRoot.isEmpty())
                return SavedStatus.build(ESaveStatus.FAILED, "Unknown Batch Process");

            GajiBatchRoot batchRoot = GajiBatchRootProcessRequest.reProcess(optionalBatchRoot.get(), request);
            repository.save(batchRoot);

            GajiBatchRoot savedBatchRoot = repository.save(batchRoot);
            kafkaTemplate.send(PENGGAJIAN_TOPIC, mapper.writeValueAsString(savedBatchRoot));
            return SavedStatus.build(ESaveStatus.SUCCESS, savedBatchRoot);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> verify1(String id, GajiBatchRootProcessRequest request) {
        try {
            Optional<GajiBatchRoot> byId = repository.findById(request.getBatchId());
            if (byId.isEmpty())
                return SavedStatus.build(ESaveStatus.FAILED, "Unknown Batch Process");
            GajiBatchRoot gajiBatchRoot = GajiBatchRootProcessRequest.verifyPhase1(byId.get(), request);
            GajiBatchRoot save = repository.save(gajiBatchRoot);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> verify2(String id, GajiBatchRootProcessRequest request) {
        try {
            Optional<GajiBatchRoot> byId = repository.findById(request.getBatchId());
            if (byId.isEmpty())
                return SavedStatus.build(ESaveStatus.FAILED, "Unknown Batch Process");
            GajiBatchRoot gajiBatchRoot = GajiBatchRootProcessRequest.verifyPhase2(byId.get(), request);
            GajiBatchRoot save = repository.save(gajiBatchRoot);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> accept(String id, GajiBatchRootProcessRequest request) {
        try {
            Optional<GajiBatchRoot> byId = repository.findById(request.getBatchId());
            if (byId.isEmpty())
                return SavedStatus.build(ESaveStatus.FAILED, "Unknown Batch Process");
            GajiBatchRoot gajiBatchRoot = GajiBatchRootProcessRequest.accept(byId.get(), request);
            GajiBatchRoot save = repository.save(gajiBatchRoot);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public boolean delete(String id) {
        Optional<GajiBatchRoot> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }
}
