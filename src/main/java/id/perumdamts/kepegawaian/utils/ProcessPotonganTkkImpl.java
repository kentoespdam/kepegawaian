package id.perumdamts.kepegawaian.utils;

import id.perumdamts.kepegawaian.entities.commons.EJenisPotonganGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootLampiran;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchRootLampiranRepository;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchPotonganTkk.GajiBatchPotonganTkkBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessPotonganTkkImpl implements ProcessPotonganTkk {
    private static final String FILE_PATH = System.getProperty("user.dir") + "/attachments/Penggajian/PotonganTKK/";
    private final GajiBatchRootLampiranRepository gajiBatchRootLampiranRepository;
    private final GajiBatchPotonganTkkBatchService batchService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void process(String rootBatchId) {
        long start = System.currentTimeMillis();
        log.info("Starting Process Potongan TKK, {}", rootBatchId);
        try {
            List<GajiBatchRootLampiran> list = gajiBatchRootLampiranRepository.findByGajiBatchRoot_IdAndJenisLampiranGaji(rootBatchId, EJenisPotonganGaji.POTONGAN_TKK);
            if (list == null || list.isEmpty()) {
                log.warn("No Potongan TKK attachment found for rootBatchId: {}", rootBatchId);
                return;
            }
            list.sort((l1, l2) -> l2.getId().compareTo(l1.getId()));
            GajiBatchRootLampiran attachment = list.getFirst();
            if (attachment == null) return;

            String period = attachment.getGajiBatchRoot().getPeriode();
            String originalFilePath = FILE_PATH + period + "/" + attachment.getHashedFileName();
            File file = new File(originalFilePath);
            if (!file.exists()) {
                log.error("Attachment file not found: {}", originalFilePath);
                return;
            }

            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                batchService.processStream(rootBatchId, fileInputStream);
            } catch (IOException e) {
                log.error("Failed to read Potongan TKK file: {}", originalFilePath, e);
                throw new RuntimeException("Failed to read spreadsheet file", e);
            }
        } finally {
            log.info("processPotonganTkk took {}ms", System.currentTimeMillis() - start);
        }
    }
}
