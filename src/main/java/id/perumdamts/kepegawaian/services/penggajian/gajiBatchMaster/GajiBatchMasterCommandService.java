package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMaster;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisPotonganGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootLampiran;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchRootLampiranRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchRootRepository;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import id.perumdamts.kepegawaian.utils.UploadResultUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class GajiBatchMasterCommandService {
    @Value("${penggajian.endpoint}")
    private String endpoint;

    private final GajiBatchRootRepository gajiBatchRootRepository;
    private final FileUploadUtil fileUploadUtil;
    private final GajiBatchRootLampiranRepository gajiBatchRootLampiranRepository;
    private final RestClient restClient;

    @Transactional
    public SavedStatus<String> uploadPotonganTambahan(String rootBatchId, GajiBatchMasterPostRequest request) {
        boolean exist = gajiBatchRootRepository.existsById(rootBatchId);
        if (!exist)
            throw new NotFoundException("Unknown Batch Id");
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
        String block = restClient.patch()
                .uri(endpoint + "/upload/" + rootBatchId + "/additional_gaji")
                .body(multipartBodyBuilder.build())
                .retrieve()
                .body(String.class);
        log.info("debugging: {}", block);
        return SavedStatus.build(ESaveStatus.SUCCESS, "1 success");
    }
}
