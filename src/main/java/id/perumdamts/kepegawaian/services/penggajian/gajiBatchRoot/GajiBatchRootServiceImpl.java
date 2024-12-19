package id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootProcessRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootResponse;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchRootRepository;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import id.perumdamts.kepegawaian.utils.ProcessPotonganTkk;
import id.perumdamts.kepegawaian.utils.UploadResultUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GajiBatchRootServiceImpl implements GajiBatchRootService {
    private final GajiBatchRootRepository repository;
    private final FileUploadUtil fileUploadUtil;
    private final ProcessPotonganTkk processPotonganTkk;

    @Override
    public Page<GajiBatchRootResponse> findAll(GajiBatchRootRequest request) {
        if (request.getSortBy() == null) {
            request.setSortBy("batchId");
            request.setSortDirection("DESC");
        }

        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(GajiBatchRootResponse::from);
    }

    @Override
    public SavedStatus<?> save(GajiBatchRootPostRequest request) {
        try {
            Optional<GajiBatchRoot> byId = repository.findById(request.getPeriode());
            if (byId.isPresent())
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Batch already exists");

            GajiBatchRoot entity = GajiBatchRootPostRequest.toEntityPhase1(request);
            repository.findDeletedBatchRoot(request.getPeriode())
                    .ifPresent(gajiBatchRoot -> {
                        String nextBatchId = request.nextBatchId(gajiBatchRoot.getBatchId());
                        entity.setBatchId(nextBatchId);
                    });

            if (request.getFile() != null) {
                UploadResultUtil uploadResultUtil = fileUploadUtil.uploadPenggajian(
                        request.getFile(),
                        "PotonganTKK/" + entity.getPeriode()
                );
                entity.setFileName(uploadResultUtil.getFileName());
                entity.setHashedFileName(uploadResultUtil.getHashedFileName());
                entity.setMimeType(uploadResultUtil.getMimeType());
            }

            GajiBatchRoot save = repository.save(entity);
            processPotonganTkk.process(save);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> reprocess(String id, GajiBatchRootProcessRequest request) {
        try {
            Optional<GajiBatchRoot> byId = repository.findById(request.getBatchId());
            if (byId.isEmpty())
                return SavedStatus.build(ESaveStatus.FAILED, "Unknown Batch Process");

            GajiBatchRoot gajiBatchRoot = GajiBatchRootProcessRequest.reProcess(byId.get(), request);
            // save old data as deleted
            byId.get().setIsDeleted(true);
            repository.save(byId.get());

            GajiBatchRoot save = repository.save(gajiBatchRoot);
            processPotonganTkk.process(save);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
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
