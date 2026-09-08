package id.perumdamts.kepegawaian.services.penggajian.gajiBatchPotonganTkk;

import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchPotonganTkk.GajiBatchPotonganTkkExcelRow;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchPotonganTkk.GajiBatchPotonganTkkItem;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchPotonganTkk.GajiBatchPotonganTkkUploadResponse;
import id.perumdamts.kepegawaian.exceptions.BadRequestException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.penggajian.jooq.GajiBatchPotonganTkkBatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.read.listener.ReadListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GajiBatchPotonganTkkBatchService {
    private final GajiBatchPotonganTkkBatchRepository batchRepository;

    private record IndexedRow(int rowNum, GajiBatchPotonganTkkExcelRow row) {}
    private record ValidatedRow(int rowNum, String nipam, Integer potongan) {}

    @Transactional
    public GajiBatchPotonganTkkUploadResponse processStream(String batchId, InputStream inputStream) {
        if (batchId == null || batchId.isBlank()) {
            throw new BadRequestException("Batch ID tidak boleh kosong");
        }
        if (inputStream == null) {
            throw new BadRequestException("Input stream tidak boleh kosong");
        }

        List<IndexedRow> rawRows = readExcel(inputStream);
        if (rawRows.isEmpty()) {
            throw new BadRequestException("File Excel tidak memiliki data");
        }

        List<String> errors = new ArrayList<>();
        List<ValidatedRow> validatedRows = new ArrayList<>();
        Set<String> seenNipams = new HashSet<>();

        for (IndexedRow indexedRow : rawRows) {
            int rowNum = indexedRow.rowNum();
            GajiBatchPotonganTkkExcelRow row = indexedRow.row();

            if (isRowEmpty(row)) {
                continue;
            }

            // NIPAM normalization & validation
            String rawNipam = row.getNipam();
            String nipam = normalizeNipam(rawNipam);
            if (nipam == null || nipam.isBlank()) {
                errors.add("Baris " + rowNum + ": NIPAM tidak boleh kosong");
            } else {
                if (!seenNipams.add(nipam)) {
                    errors.add("Baris " + rowNum + ": NIPAM '" + nipam + "' duplikat di dalam file");
                }
            }

            // Potongan validation
            Double rawPotongan = row.getPotongan();
            Integer potongan = null;
            if (rawPotongan == null) {
                errors.add("Baris " + rowNum + ": Nilai potongan tidak boleh kosong");
            } else if (rawPotongan < 0) {
                errors.add("Baris " + rowNum + ": Nilai potongan tidak boleh negatif");
            } else {
                potongan = (int) Math.round(rawPotongan);
            }

            if (nipam != null && !nipam.isBlank() && potongan != null) {
                validatedRows.add(new ValidatedRow(rowNum, nipam, potongan));
            }
        }

        if (validatedRows.isEmpty() && errors.isEmpty()) {
            throw new BadRequestException("File Excel tidak memiliki data");
        }

        // Validate NIPAM against master pegawai in chunks of 1000
        List<String> distinctNipams = validatedRows.stream()
                .map(ValidatedRow::nipam)
                .distinct()
                .toList();
        Set<String> existingPegawaiNipams = new HashSet<>();
        int chunkSize = 1000;
        for (int i = 0; i < distinctNipams.size(); i += chunkSize) {
            List<String> chunk = distinctNipams.subList(i, Math.min(i + chunkSize, distinctNipams.size()));
            existingPegawaiNipams.addAll(batchRepository.findExistingPegawaiNipams(chunk));
        }

        for (ValidatedRow vRow : validatedRows) {
            if (!existingPegawaiNipams.contains(vRow.nipam())) {
                errors.add("Baris " + vRow.rowNum() + ": NIPAM '" + vRow.nipam() + "' tidak terdaftar di sistem");
            }
        }

        if (!errors.isEmpty()) {
            throw new BadRequestException(formatErrorMessage(errors));
        }

        List<GajiBatchPotonganTkkItem> items = validatedRows.stream()
                .map(v -> new GajiBatchPotonganTkkItem(v.nipam(), v.potongan()))
                .toList();

        batchRepository.deleteByBatchId(batchId);
        int totalInserted = batchRepository.batchInsert(batchId, items);

        return new GajiBatchPotonganTkkUploadResponse(batchId, items.size(), totalInserted);
    }

    public Resource getTemplateResource() {
        Resource resource = new ClassPathResource("templates/excel/template_potongan_tkk.xlsx");
        if (!resource.exists()) {
            throw new NotFoundException("Template potongan TKK tidak ditemukan");
        }
        return resource;
    }

    public String normalizeNipam(String rawNipam) {
        if (rawNipam == null || rawNipam.isBlank()) {
            return null;
        }
        String n = rawNipam.trim();
        if (n.endsWith(".0")) {
            n = n.substring(0, n.length() - 2).trim();
        }
        if (n.matches("^\\d+$") && n.length() < 9) {
            n = "0".repeat(9 - n.length()) + n;
        }
        return n;
    }

    private List<IndexedRow> readExcel(InputStream inputStream) {
        List<IndexedRow> rows = new ArrayList<>();
        try {
            FesodSheet.read(inputStream, GajiBatchPotonganTkkExcelRow.class, new ReadListener<GajiBatchPotonganTkkExcelRow>() {
                @Override
                public void invoke(GajiBatchPotonganTkkExcelRow data, AnalysisContext context) {
                    int rowNum = (context != null && context.readRowHolder() != null && context.readRowHolder().getRowIndex() != null)
                            ? context.readRowHolder().getRowIndex() + 1
                            : 5 + rows.size();
                    rows.add(new IndexedRow(rowNum, data));
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {}
            }).headRowNumber(4).sheet(0).doRead();
        } catch (Exception e) {
            log.error("Failed to read Excel stream", e);
            throw new BadRequestException("Gagal memproses file Excel: " + e.getMessage());
        }
        return rows;
    }

    private boolean isRowEmpty(GajiBatchPotonganTkkExcelRow row) {
        if (row == null) return true;
        boolean noEmpty = row.getNo() == null || row.getNo().trim().isEmpty();
        boolean nipamEmpty = row.getNipam() == null || row.getNipam().trim().isEmpty();
        boolean namaEmpty = row.getNama() == null || row.getNama().trim().isEmpty();
        boolean potonganEmpty = row.getPotongan() == null;
        return noEmpty && nipamEmpty && namaEmpty && potonganEmpty;
    }

    private String formatErrorMessage(List<String> errors) {
        StringBuilder sb = new StringBuilder();
        int limit = Math.min(errors.size(), 20);
        for (int i = 0; i < limit; i++) {
            if (i > 0) {
                sb.append("\n");
            }
            sb.append(errors.get(i));
        }
        if (errors.size() > 20) {
            int remaining = errors.size() - 20;
            sb.append("\n... dan ").append(remaining).append(" kesalahan lainnya");
        }
        return sb.toString();
    }
}
