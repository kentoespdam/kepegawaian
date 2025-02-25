package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMaster;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterResponse;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchMasterRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchRootRepository;
import id.perumdamts.kepegawaian.utils.DownloadPenggajian;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GajiBatchMasterServiceImpl implements GajiBatchMasterService {
    private final GajiBatchMasterRepository repository;
    private final DownloadPenggajian downloadPenggajian;
    private final GajiBatchRootRepository gajiBatchRootRepository;
    private final FileUploadUtil fileUploadUtil;

    @Override
    public List<GajiBatchMasterResponse> findAll(GajiBatchMasterRequest request) {
        return repository.findAll(request.getSpecification()).stream().map(GajiBatchMasterResponse::from).toList();
    }

    @Override
    public GajiBatchMasterResponse findById(Long id) {
        return repository.findById(id).map(GajiBatchMasterResponse::from).orElse(null);
    }

    @Override
    public ResponseEntity<?> downloadTableGaji(String rootBatchId) {
        try {
            Flux<ByteArrayResource> byteArrayResourceFlux = downloadPenggajian.downloadTableGaji(rootBatchId);
            ByteArrayResource byteArrayResource = byteArrayResourceFlux.blockFirst();
            assert byteArrayResource != null;
            return ResponseEntity.ok()
                    .contentLength(byteArrayResource.contentLength())
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("Content-Disposition", "attachment; filename=\"table_gaji_" + rootBatchId + ".xlsx\"")
                    .body(byteArrayResource);
        } catch (Exception e) {
            return ErrorResult.build(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> downloadPotonganGaji(String rootBatchId) {
        try {
            Flux<ByteArrayResource> byteArrayResourceFlux = downloadPenggajian.downloadPotonganGaji(rootBatchId);
            ByteArrayResource byteArrayResource = byteArrayResourceFlux.blockFirst();
            assert byteArrayResource != null;
            return ResponseEntity.ok()
                    .contentLength(byteArrayResource.contentLength())
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("Content-Disposition", "attachment; filename=\"potongan_gaji_" + rootBatchId + ".xlsx\"")
                    .body(byteArrayResource);
        } catch (Exception e) {
            return ErrorResult.build(e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> uploadPotonganTambahan(String rootBatchId, GajiBatchMasterPostRequest request) {
        try {
            boolean exist = gajiBatchRootRepository.existsById(rootBatchId);
            if (!exist)
                throw new RuntimeException("Unknown Batch Id");
            fileUploadUtil.uploadPenggajian(request.getFile(), "potongan_tambahan" + rootBatchId.split("-")[0]);
            return null;

        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }
}
