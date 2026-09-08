package id.perumdamts.kepegawaian.services.penggajian.gajiKpi;

import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiExcelRow;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiItem;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiUploadResponse;
import id.perumdamts.kepegawaian.exceptions.BadRequestException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.penggajian.jooq.GajiKpiBatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.read.listener.ReadListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GajiKpiBatchService {
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024L; // 50MB
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-excel",
            "application/octet-stream",
            "application/x-tika-ooxml",
            "application/wps-office.xlsx",
            "application/wps-office.xls"
    );

    private final GajiKpiBatchRepository batchRepository;

    private record IndexedRow(int rowNum, GajiKpiExcelRow row) {}
    private record ValidatedRow(int rowNum, String nipam, String periode, Double tunkin, Double pph21Ter) {}

    @Transactional
    public GajiKpiUploadResponse upload(MultipartFile file, String requestedPeriode, String currentUser) {
        validateFile(file);
        String normalizedPeriode = normalizeRequestedPeriode(requestedPeriode);

        List<IndexedRow> rawRows = readExcel(file);
        if (rawRows.isEmpty()) {
            throw new BadRequestException("File Excel tidak memiliki data");
        }

        List<String> errors = new ArrayList<>();
        List<ValidatedRow> validatedRows = new ArrayList<>();
        Set<String> seenNipams = new HashSet<>();

        for (IndexedRow indexedRow : rawRows) {
            int rowNum = indexedRow.rowNum();
            GajiKpiExcelRow row = indexedRow.row();

            if (isRowEmpty(row)) {
                continue;
            }

            // NIPAM normalization
            String rawNipam = row.getNipam();
            String nipam = normalizeNipam(rawNipam);
            if (nipam == null || nipam.isBlank()) {
                errors.add("Baris " + rowNum + ": NIPAM tidak boleh kosong");
            } else {
                if (!seenNipams.add(nipam)) {
                    errors.add("Baris " + rowNum + ": NIPAM '" + nipam + "' duplikat di dalam file");
                }
            }

            // Periode normalization
            String rawRowPeriode = row.getPeriode();
            String rowPeriode = normalizeRowPeriode(rawRowPeriode);
            if (rowPeriode == null || !rowPeriode.equals(normalizedPeriode)) {
                errors.add("Baris " + rowNum + ": Periode '" + (rowPeriode != null ? rowPeriode : "") + "' tidak sesuai dengan periode request '" + normalizedPeriode + "'");
            }

            // Tunkin validation
            Double tunkin = row.getTunkin();
            if (tunkin == null) {
                errors.add("Baris " + rowNum + ": Tunkin tidak boleh kosong");
            } else if (tunkin < 0) {
                errors.add("Baris " + rowNum + ": Tunkin tidak boleh negatif");
            }

            // Pph21Ter validation
            double pph21Ter = row.getPph21Ter() != null ? row.getPph21Ter() : 0.0;
            if (pph21Ter < 0) {
                errors.add("Baris " + rowNum + ": PPh 21 TER tidak boleh negatif");
            }

            if (nipam != null && !nipam.isBlank()) {
                validatedRows.add(new ValidatedRow(rowNum, nipam, normalizedPeriode, tunkin, pph21Ter));
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

        List<GajiKpiItem> items = validatedRows.stream()
                .map(v -> new GajiKpiItem(v.nipam(), v.periode(), v.tunkin(), v.pph21Ter()))
                .toList();

        return batchRepository.batchUpsert(normalizedPeriode, items, currentUser);
    }

    public Resource getTemplateResource() {
        Resource resource = new ClassPathResource("templates/excel/Tunkin_Template.xlsx");
        if (!resource.exists()) {
            throw new NotFoundException("Template Tunkin tidak ditemukan");
        }
        return resource;
    }

    public String normalizeRequestedPeriode(String requestedPeriode) {
        if (requestedPeriode == null || requestedPeriode.isBlank()) {
            throw new BadRequestException("Format periode tidak valid: " + requestedPeriode);
        }
        String trimmed = requestedPeriode.trim();
        if (trimmed.matches("^\\d{6}$")) {
            String year = trimmed.substring(0, 4);
            String month = trimmed.substring(4, 6);
            int m = Integer.parseInt(month);
            if (m < 1 || m > 12) {
                throw new BadRequestException("Format periode tidak valid: " + requestedPeriode);
            }
            return year + "-" + month;
        }
        if (trimmed.matches("^\\d{4}-(0[1-9]|1[0-2])$")) {
            return trimmed;
        }
        throw new BadRequestException("Format periode tidak valid: " + requestedPeriode);
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

    public String normalizeRowPeriode(String rawPeriode) {
        if (rawPeriode == null || rawPeriode.isBlank()) {
            return "";
        }
        String p = rawPeriode.trim();
        if (p.endsWith(".0")) {
            p = p.substring(0, p.length() - 2).trim();
        }
        if (p.matches("^\\d+$") && p.length() < 6) {
            p = "0".repeat(6 - p.length()) + p;
        }
        if (p.matches("^\\d{6}$")) {
            p = p.substring(0, 4) + "-" + p.substring(4, 6);
        }
        return p;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File tidak boleh kosong");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.toLowerCase().endsWith(".xlsx") && !filename.toLowerCase().endsWith(".xls"))) {
            throw new BadRequestException("Format file harus berupa Excel (.xlsx atau .xls)");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("Ukuran file melebihi batas maksimum 50MB");
        }
        String contentType = file.getContentType();
        if (contentType != null && !contentType.isBlank()) {
            String ct = contentType.toLowerCase().trim();
            boolean validMime = ALLOWED_MIME_TYPES.contains(ct)
                    || ct.contains("spreadsheetml")
                    || ct.contains("excel");
            if (!validMime) {
                throw new BadRequestException("Tipe file tidak valid: " + contentType);
            }
        }
    }

    private List<IndexedRow> readExcel(MultipartFile file) {
        List<IndexedRow> rows = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream()) {
            FesodSheet.read(inputStream, GajiKpiExcelRow.class, new ReadListener<GajiKpiExcelRow>() {
                @Override
                public void invoke(GajiKpiExcelRow data, AnalysisContext context) {
                    int rowNum = (context != null && context.readRowHolder() != null && context.readRowHolder().getRowIndex() != null)
                            ? context.readRowHolder().getRowIndex() + 1
                            : 6 + rows.size();
                    rows.add(new IndexedRow(rowNum, data));
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {}
            }).headRowNumber(5).sheet(0).doRead();
        } catch (IOException e) {
            log.error("Failed to read Excel file", e);
            throw new BadRequestException("Gagal membaca file Excel: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error processing Excel file", e);
            throw new BadRequestException("Gagal memproses file Excel: " + e.getMessage());
        }
        return rows;
    }

    private boolean isRowEmpty(GajiKpiExcelRow row) {
        if (row == null) return true;
        boolean noEmpty = row.getNo() == null || row.getNo().trim().isEmpty();
        boolean periodeEmpty = row.getPeriode() == null || row.getPeriode().trim().isEmpty();
        boolean nipamEmpty = row.getNipam() == null || row.getNipam().trim().isEmpty();
        boolean namaEmpty = row.getNama() == null || row.getNama().trim().isEmpty();
        boolean tunkinEmpty = row.getTunkin() == null;
        boolean pphEmpty = row.getPph21Ter() == null;
        return noEmpty && periodeEmpty && nipamEmpty && namaEmpty && tunkinEmpty && pphEmpty;
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
