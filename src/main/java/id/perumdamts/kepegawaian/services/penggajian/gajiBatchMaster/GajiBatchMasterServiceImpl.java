package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMaster;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisPotonganGaji;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootLampiran;
import id.perumdamts.kepegawaian.repositories.master.OrganisasiRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchMasterRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchRootLampiranRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchRootRepository;
import id.perumdamts.kepegawaian.utils.DownloadPenggajian;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import id.perumdamts.kepegawaian.utils.UploadResultUtil;
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
    private final GajiBatchRootLampiranRepository gajiBatchRootLampiranRepository;
    private final OrganisasiRepository organisasiRepository;

    @Override
    public List<GajiBatchMasterResponse> findAll(GajiBatchMasterRequest request) {
        return repository.findAll(request.getSpecification()).stream().map(gbm -> {
            Organisasi organisasi = organisasiRepository.findById(gbm.getOrganisasiId()).orElse(null);
            return GajiBatchMasterResponse.from(gbm, organisasi);
        }).toList();
    }

    @Override
    public GajiBatchMasterResponse findById(Long id) {
        return repository.findById(id).map(gbm -> {
            Organisasi organisasi = organisasiRepository.findById(gbm.getOrganisasiId()).orElse(null);
            return GajiBatchMasterResponse.from(gbm, organisasi);
        }).orElse(null);
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
            UploadResultUtil uploadResultUtil = fileUploadUtil.uploadPenggajian(request.getFile(), "potongan/tambahan/" + rootBatchId.split("-")[0]);
            if (!uploadResultUtil.isSuccess())
                throw new RuntimeException(uploadResultUtil.getMessage());
            GajiBatchRootLampiran gajiBatchRootLampiran = new GajiBatchRootLampiran();
            gajiBatchRootLampiran.setGajiBatchRoot(gajiBatchRootRepository.findById(rootBatchId).orElse(null));
            gajiBatchRootLampiran.setJenisLampiranGaji(EJenisPotonganGaji.POTONGAN_TAMBAHAN);
            gajiBatchRootLampiran.setFileName(uploadResultUtil.getFileName());
            gajiBatchRootLampiran.setMimeType(uploadResultUtil.getMimeType());
            gajiBatchRootLampiran.setHashedFileName(uploadResultUtil.getHashedFileName());
            gajiBatchRootLampiranRepository.save(gajiBatchRootLampiran);
            return SavedStatus.build(ESaveStatus.SUCCESS, "OK");

        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }
}
