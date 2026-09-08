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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;
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

    private static final Pattern SLUG_TOKEN = Pattern.compile("[^A-Z0-9]+");
    private static final String KODE_PREFIX = "ADD_";
    private static final int LABEL_START_COLUMN = 4; // kolom E

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

        List<SheetRowBuffer> sheetBuffers = readAllSheets(inputStream);
        if (sheetBuffers.isEmpty()) {
            throw new BadRequestException("File Excel tidak memiliki data");
        }

        StatsAndErrors parsed = parseAllSheets(rootBatchId, sheetBuffers);
        if (parsed.errors().isEmpty() && parsed.items().isEmpty()) {
            throw new BadRequestException("File Excel tidak memiliki data");
        }
        if (!parsed.errors().isEmpty()) {
            throw new BadRequestException(formatErrorMessage(parsed.errors()));
        }

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

    private List<SheetRowBuffer> readAllSheets(InputStream inputStream) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            List<SheetRowBuffer> buffers = new ArrayList<>();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                buffers.add(new PoiSheetBuffer(workbook.getSheetAt(i)));
            }
            return buffers;
        } catch (IOException e) {
            log.error("Failed to read Excel stream", e);
            throw new BadRequestException("Gagal memproses file Excel: " + e.getMessage());
        }
    }

    private StatsAndErrors parseAllSheets(String rootBatchId, List<SheetRowBuffer> buffers) {
        List<GajiBatchPotonganTambahanItem> items = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        for (SheetRowBuffer buf : buffers) {
            SheetParseResult result = parseSheet(rootBatchId, buf);
            items.addAll(result.items());
            errors.addAll(result.errors());
        }
        // duplikat antar-sheet: NIPAM yang sama muncul di sheet berbeda = error (multi-rows per NIPAM dalam satu sheet adalah normal)
        Map<String, Set<String>> sheetsByNipam = new HashMap<>();
        for (GajiBatchPotonganTambahanItem item : items) {
            sheetsByNipam.computeIfAbsent(item.nipam(), k -> new HashSet<>()).add(item.sheetName());
        }
        for (Map.Entry<String, Set<String>> entry : sheetsByNipam.entrySet()) {
            if (entry.getValue().size() > 1) {
                errors.add("NIPAM '" + entry.getKey() + "' duplikat antar sheet: " + String.join(", ", entry.getValue()));
            }
        }
        return new StatsAndErrors(items, errors);
    }

    private SheetParseResult parseSheet(String rootBatchId, SheetRowBuffer buf) {
        List<GajiBatchPotonganTambahanItem> items = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        HeaderInfo header = detectHeader(buf);
        if (header == null) {
            errors.add("Sheet '" + buf.sheetName() + "': tidak menemukan header (NAMA/NIPAM)");
            return new SheetParseResult(items, errors);
        }
        int firstDataRow = header.labelRow() + 1;

        for (int i = firstDataRow; i < buf.rowCount(); i++) {
            String nama = buf.namaAt(i);
            String nipam = buf.nipamAt(i);
            if (isBlank(nama) && isBlank(nipam)) {
                // baris total / pemisah: kolom B & C kosong → bukan baris data, skip
                continue;
            }

            String normNipam = normalizeNipam(nipam);
            if (normNipam == null) {
                errors.add("Baris " + (i + 1) + " sheet '" + buf.sheetName() + "': NIPAM tidak boleh kosong");
                continue;
            }

            for (int c = 0; c < header.labels().size(); c++) {
                Double nilai = buf.potAt(i, LABEL_START_COLUMN + c);
                if (nilai == null) {
                    continue;
                }
                if (nilai < 0) {
                    errors.add("Baris " + (i + 1) + " sheet '" + buf.sheetName() + "': nilai potongan negatif pada kolom '" + header.labels().get(c) + "'");
                    continue;
                }
                if (nilai == 0) {
                    continue;
                }
                String kode = slugCode(header.labels().get(c));
                items.add(new GajiBatchPotonganTambahanItem(buf.sheetName(), rootBatchId, normNipam, kode, header.labels().get(c), nilai));
            }
        }

        return new SheetParseResult(items, errors);
    }

    private HeaderInfo detectHeader(SheetRowBuffer buf) {
        int rowCount = buf.rowCount();
        for (int i = 0; i < rowCount; i++) {
            String b = buf.namaAt(i);  // kolom B
            String c = buf.nipamAt(i); // kolom C
            if ("NAMA".equalsIgnoreCase(b) && "NIPAM".equalsIgnoreCase(c)) {
                // label = baris berikutnya, mulai kolom E sampai kolom berlabel terakhir
                int labelRow = i + 1;
                if (labelRow >= rowCount) {
                    return null;
                }
                List<String> labels = new ArrayList<>();
                for (int col = LABEL_START_COLUMN; col < buf.columnCount(); col++) {
                    String val = buf.potLabelAt(labelRow, col);
                    if (val == null || val.isBlank()) {
                        break;
                    }
                    labels.add(val.trim());
                }
                if (labels.isEmpty()) {
                    return null;
                }
                return new HeaderInfo(labelRow, labels);
            }
        }
        return null;
    }

    private String normalizeNipam(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String n = raw.trim();
        if (n.endsWith(".0")) {
            n = n.substring(0, n.length() - 2).trim();
        }
        if (n.matches("^\\d+$") && n.length() < 9) {
            n = "0".repeat(9 - n.length()) + n;
        }
        return n;
    }

    private String slugCode(String label) {
        if (label == null || label.isBlank()) return KODE_PREFIX + "UNKNOWN";
        String upper = label.toUpperCase();
        String collapsed = SLUG_TOKEN.matcher(upper).replaceAll("_");
        while (collapsed.contains("__")) {
            collapsed = collapsed.replace("__", "_");
        }
        if (collapsed.startsWith("_")) collapsed = collapsed.substring(1);
        if (collapsed.endsWith("_")) collapsed = collapsed.substring(0, collapsed.length() - 1);
        if (collapsed.isBlank()) return KODE_PREFIX + "UNKNOWN";
        return KODE_PREFIX + collapsed;
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
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

    // --- inner types ---

    private record StatsAndErrors(List<GajiBatchPotonganTambahanItem> items, List<String> errors) {}
    private record SheetParseResult(List<GajiBatchPotonganTambahanItem> items, List<String> errors) {}
    private record HeaderInfo(int labelRow, List<String> labels) {}

    public interface SheetRowBuffer {
        String sheetName();
        int rowCount();
        int columnCount();
        String namaAt(int rowIndex);
        String nipamAt(int rowIndex);
        Double potAt(int rowIndex, int columnIndex);
        String potLabelAt(int rowIndex, int columnIndex);
    }

    private static class PoiSheetBuffer implements SheetRowBuffer {
        private final org.apache.poi.ss.usermodel.Sheet sheet;

        private PoiSheetBuffer(org.apache.poi.ss.usermodel.Sheet sheet) {
            this.sheet = sheet;
        }

        @Override
        public String sheetName() {
            return sheet.getSheetName();
        }

        @Override
        public int rowCount() {
            // lastRowNum adalah indeks baris terakhir — +1 supaya loop mencakup seluruh baris
            return Math.max(sheet.getLastRowNum(), 0) + 1;
        }

        @Override
        public int columnCount() {
            int max = 0;
            for (int i = 0; i < rowCount(); i++) {
                org.apache.poi.ss.usermodel.Row row = sheet.getRow(i);
                if (row != null) {
                    int last = row.getLastCellNum();
                    if (last > max) max = last;
                }
            }
            return max;
        }

        @Override
        public String namaAt(int rowIndex) {
            return cellToString(sheet.getRow(rowIndex), 1);
        }

        @Override
        public String nipamAt(int rowIndex) {
            return cellToString(sheet.getRow(rowIndex), 2);
        }

        @Override
        public Double potAt(int rowIndex, int columnIndex) {
            return cellToDouble(sheet.getRow(rowIndex), columnIndex);
        }

        @Override
        public String potLabelAt(int rowIndex, int columnIndex) {
            return cellToString(sheet.getRow(rowIndex), columnIndex);
        }

        private String cellToString(org.apache.poi.ss.usermodel.Row row, int col) {
            if (row == null) return null;
            org.apache.poi.ss.usermodel.Cell cell = row.getCell(col);
            if (cell == null) return null;
            return switch (cell.getCellType()) {
                case STRING -> cell.getStringCellValue();
                case NUMERIC -> {
                    double v = cell.getNumericCellValue();
                    if (Math.floor(v) == v && !Double.isInfinite(v)) {
                        yield String.valueOf((long) v);
                    }
                    yield String.valueOf(v);
                }
                case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                case FORMULA -> {
                    try {
                        yield cell.getCellFormula();
                    } catch (Exception e) {
                        yield null;
                    }
                }
                default -> null;
            };
        }

        private Double cellToDouble(org.apache.poi.ss.usermodel.Row row, int col) {
            if (row == null) return null;
            org.apache.poi.ss.usermodel.Cell cell = row.getCell(col);
            if (cell == null) return null;
            return switch (cell.getCellType()) {
                case NUMERIC -> cell.getNumericCellValue();
                case FORMULA -> {
                    try {
                        yield cell.getNumericCellValue();
                    } catch (Exception e) {
                        yield null;
                    }
                }
                default -> null;
            };
        }
    }
}