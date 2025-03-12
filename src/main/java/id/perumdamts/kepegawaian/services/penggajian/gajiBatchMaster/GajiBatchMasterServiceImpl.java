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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class GajiBatchMasterServiceImpl implements GajiBatchMasterService {
    @Value("${penggajian.endpoint}")
    private String ENDPOINT;
    @Autowired
    private GajiBatchMasterRepository repository;
    @Autowired
    private DownloadPenggajian downloadPenggajian;
    @Autowired
    private GajiBatchRootRepository gajiBatchRootRepository;
    @Autowired
    private FileUploadUtil fileUploadUtil;
    @Autowired
    private GajiBatchRootLampiranRepository gajiBatchRootLampiranRepository;
    @Autowired
    private WebClient webClient;
    @Autowired
    private OrganisasiRepository organisasiRepository;

    @Override
    public List<GajiBatchMasterResponse> findAll(GajiBatchMasterRequest request) {
        Optional<Organisasi> organisasiOptional = organisasiRepository.findById(request.getOrganisasiId());
        if (organisasiOptional.isPresent()) {
            Organisasi organisasi = organisasiOptional.get();
            if (organisasi.getLevelOrg() == 4) {
                request.setOrganisasiKode(organisasi.getKode());
            } else if (organisasi.getLevelOrg() == 5) {
                Organisasi parent = organisasi.getParent();
                request.setOrganisasiKode(parent.getKode());
            }
        }
        return repository.findAll(request.getSpecification()).stream()
                .map(GajiBatchMasterResponse::from)
                .toList();
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
            UploadResultUtil uploadResultUtil = fileUploadUtil.uploadPenggajian(
                    request.getFile(),
                    "potongan/tambahan/" + rootBatchId.split("-")[0]
            );
            if (!uploadResultUtil.isSuccess())
                throw new RuntimeException(uploadResultUtil.getMessage());
            GajiBatchRootLampiran gajiBatchRootLampiran = new GajiBatchRootLampiran();
            gajiBatchRootLampiran.setGajiBatchRoot(gajiBatchRootRepository.findById(rootBatchId).orElse(null));
            gajiBatchRootLampiran.setJenisLampiranGaji(EJenisPotonganGaji.POTONGAN_TAMBAHAN);
            gajiBatchRootLampiran.setFileName(uploadResultUtil.getFileName());
            gajiBatchRootLampiran.setMimeType(uploadResultUtil.getMimeType());
            gajiBatchRootLampiran.setHashedFileName(uploadResultUtil.getHashedFileName());
            gajiBatchRootLampiranRepository.save(gajiBatchRootLampiran);

            MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
            multipartBodyBuilder.part("file", request.getFile().getResource());
            String block = webClient.patch()
                    .uri(ENDPOINT + "/upload/" + rootBatchId + "/additional_gaji")
                    .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                    .exchangeToMono(clientResponse -> {
                        log.info("debugging: {}", clientResponse.statusCode());
                        return clientResponse.bodyToMono(String.class);
                    }).block();
            log.info("debugging: {}", block);
            return SavedStatus.build(ESaveStatus.SUCCESS, "OK");

        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }
}
