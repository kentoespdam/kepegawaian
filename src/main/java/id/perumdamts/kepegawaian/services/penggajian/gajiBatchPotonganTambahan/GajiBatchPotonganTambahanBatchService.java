package id.perumdamts.kepegawaian.services.penggajian.gajiBatchPotonganTambahan;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchPotonganTambahan.GajiBatchPotonganTambahanItem;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchPotonganTambahan.GajiBatchPotonganTambahanUploadResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisPotonganGaji;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootLampiran;
import id.perumdamts.kepegawaian.exceptions.BadRequestException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchRootLampiranRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jooq.GajiBatchPotonganTambahanBatchRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchMasterRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchRootRepository;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesCommandService;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import id.perumdamts.kepegawaian.utils.UploadResultUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GajiBatchPotonganTambahanBatchService {
    private final GajiBatchRootRepository rootRepository;
    private final GajiBatchMasterRepository masterRepository;
    private final GajiBatchRootLampiranRepository lampiranRepository;
    private final GajiBatchPotonganTambahanBatchRepository tambahanBatchRepository;
    private final GajiBatchMasterProsesCommandService prosesCommandService;
    private final FileUploadUtil fileUploadUtil;
    private final GajiBatchPotonganTambahanTemplateGenerator templateGenerator;
    private final GajiBatchPotonganTambahanExcelParser excelParser;

    @Transactional
    public SavedStatus<String> upload(MultipartFile file, String rootBatchId) {
        GajiBatchRoot root = rootRepository.findById(rootBatchId)
                .orElseThrow(() -> new NotFoundException("Root batch tidak ditemukan: " + rootBatchId));
        UploadResultUtil uploadResult = fileUploadUtil.uploadPenggajian(file, "potongan/tambahan/" + rootBatchId.split("-")[0]);
        if (!uploadResult.isSuccess()) {
            throw new RuntimeException(uploadResult.getMessage());
        }
        // simpan lampiran POTONGAN_TAMBAHAN
        GajiBatchRootLampiran lampiran = new GajiBatchRootLampiran(
                root,
                EJenisPotonganGaji.POTONGAN_TAMBAHAN,
                uploadResult.getMimeType(),
                uploadResult.getFileName(),
                uploadResult.getHashedFileName()
        );
        lampiranRepository.save(lampiran);
        try (InputStream in = file.getInputStream()) {
            GajiBatchPotonganTambahanUploadResponse resp = process(in, rootBatchId);
            return SavedStatus.build(ESaveStatus.SUCCESS, resp.totalRows() + " success");
        } catch (IOException e) {
            log.error("Failed to read Excel stream", e);
            throw new BadRequestException("Gagal memproses file Excel: " + e.getMessage());
        }
    }

    public GajiBatchPotonganTambahanUploadResponse process(InputStream inputStream, String rootBatchId) {
        if (rootBatchId == null || rootBatchId.isBlank()) {
            throw new BadRequestException("Root batch ID tidak boleh kosong");
        }
        if (inputStream == null) {
            throw new BadRequestException("Input stream tidak boleh kosong");
        }

        GajiBatchRoot root = rootRepository.findById(rootBatchId)
                .orElseThrow(() -> new NotFoundException("Root batch tidak ditemukan: " + rootBatchId));

        // guard: hanya WAIT_VERIFICATION_PHASE_2 yang boleh upload
        if (root.getStatus() != EProsesGaji.WAIT_VERIFICATION_PHASE_2) {
            throw new BadRequestException(formatStatusError(root.getStatus()));
        }

        GajiBatchPotonganTambahanExcelParser.StatsAndErrors parsed = excelParser.parse(inputStream, rootBatchId);

        // NIPAM wajib ada di GajiBatchMaster milik batch ini (satu query, dipakai ulang untuk recalculate)
        List<GajiBatchMaster> masters = masterRepository.findByGajiBatchRoot_Id(root.getId());
        Set<String> batchNipams = masters.stream().map(GajiBatchMaster::getNipam).collect(Collectors.toSet());

        List<String> missingErrors = new ArrayList<>();
        List<GajiBatchPotonganTambahanItem> validItems = new ArrayList<>();
        for (GajiBatchPotonganTambahanItem item : parsed.items()) {
            if (!batchNipams.contains(item.nipam())) {
                missingErrors.add("NIPAM '" + item.nipam() + "' tidak ada di batch ini");
            } else {
                validItems.add(item);
            }
        }
        if (!missingErrors.isEmpty()) {
            throw new BadRequestException(formatErrorMessage(missingErrors));
        }
        if (validItems.isEmpty()) {
            throw new BadRequestException("File Excel tidak memiliki data");
        }

        // replace: hapus ADD_% milik batch, catat dulu master yang kehilangan baris ADD_ agar ikut di-recalculate
        Set<Long> priorAddMasterIds = tambahanBatchRepository.findMasterIdsWithAddRows(root.getId());
        tambahanBatchRepository.deleteByRootBatchId(root.getId());

        // insert ke GajiBatchMasterProses
        int inserted = tambahanBatchRepository.batchInsert(root.getId(), validItems);

        // recalculate per master terdampak: yang punya ADD_% sebelumnya + yang ada di file
        Set<String> affectedNipams = validItems.stream().map(GajiBatchPotonganTambahanItem::nipam).collect(Collectors.toSet());
        for (GajiBatchMaster m : masters) {
            if (affectedNipams.contains(m.getNipam()) || priorAddMasterIds.contains(m.getId())) {
                prosesCommandService.recalculateAdditional(m);
            }
        }

        return new GajiBatchPotonganTambahanUploadResponse(rootBatchId, inserted);
    }

    public Resource getTemplateResource(String rootBatchId) {
        try {
            return new ByteArrayResource(templateGenerator.generateForRootBatchId(rootBatchId));
        } catch (IOException e) {
            log.error("Failed to generate template for root batch {}", rootBatchId, e);
            throw new BadRequestException("Gagal generate template: " + e.getMessage());
        }
    }

    private String formatStatusError(EProsesGaji status) {
        return "Upload potongan tambahan hanya diizinkan untuk status WAIT_VERIFICATION_PHASE_2 (status saat ini: " + status + ")";
    }

    private String formatErrorMessage(List<String> errors) {
        StringBuilder sb = new StringBuilder();
        int limit = Math.min(errors.size(), 20);
        for (int i = 0; i < limit; i++) {
            if (i > 0) sb.append("\n");
            sb.append(errors.get(i));
        }
        if (errors.size() > 20) {
            sb.append("\n... dan ").append(errors.size() - 20).append(" kesalahan lainnya");
        }
        return sb.toString();
    }
}