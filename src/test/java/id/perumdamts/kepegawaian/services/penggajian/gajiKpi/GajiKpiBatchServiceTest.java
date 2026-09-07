package id.perumdamts.kepegawaian.services.penggajian.gajiKpi;

import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiItem;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiUploadResponse;
import id.perumdamts.kepegawaian.exceptions.BadRequestException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.penggajian.jooq.GajiKpiBatchRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GajiKpiBatchServiceTest {

    @Mock
    private GajiKpiBatchRepository batchRepository;

    private GajiKpiBatchService service;

    @BeforeEach
    void setUp() {
        service = new GajiKpiBatchService(batchRepository);
    }

    private MockMultipartFile createExcelFile(String filename, String contentType, List<List<Object>> rows) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Sheet1");
            for (int r = 0; r < 5; r++) {
                Row headerRow = sheet.createRow(r);
                Cell c = headerRow.createCell(0);
                c.setCellValue("Header " + (r + 1));
            }
            if (rows != null) {
                for (int r = 0; r < rows.size(); r++) {
                    Row dataRow = sheet.createRow(5 + r);
                    List<Object> cellValues = rows.get(r);
                    if (cellValues != null) {
                        for (int c = 0; c < cellValues.size(); c++) {
                            Object val = cellValues.get(c);
                            if (val != null) {
                                Cell cell = dataRow.createCell(3 + c);
                                if (val instanceof Number n) {
                                    cell.setCellValue(n.doubleValue());
                                } else {
                                    cell.setCellValue(val.toString());
                                }
                            }
                        }
                    }
                }
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new MockMultipartFile("file", filename, contentType, out.toByteArray());
        }
    }

    @Nested
    @DisplayName("File Validation")
    class FileValidationTests {

        @Test
        void upload_nullFile_throwsBadRequest() {
            assertThrows(BadRequestException.class, () -> service.upload(null, "2026-01", "admin"));
        }

        @Test
        void upload_emptyFile_throwsBadRequest() {
            MockMultipartFile emptyFile = new MockMultipartFile("file", "test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);
            assertThrows(BadRequestException.class, () -> service.upload(emptyFile, "2026-01", "admin"));
        }

        @Test
        void upload_invalidExtension_throwsBadRequest() {
            MockMultipartFile badExtFile = new MockMultipartFile("file", "test.pdf",
                    "application/pdf", new byte[]{1, 2, 3});
            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> service.upload(badExtFile, "2026-01", "admin"));
            assertTrue(ex.getMessage().contains("Format file harus berupa Excel"));
        }

        @Test
        void upload_invalidMimeType_throwsBadRequest() {
            MockMultipartFile badMimeFile = new MockMultipartFile("file", "test.xlsx",
                    "image/png", new byte[]{1, 2, 3});
            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> service.upload(badMimeFile, "2026-01", "admin"));
            assertTrue(ex.getMessage().contains("Tipe file tidak valid"));
        }

        @Test
        void upload_oversizedFile_throwsBadRequest() {
            MockMultipartFile bigFile = new MockMultipartFile("file", "test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    new byte[51 * 1024 * 1024]) {
                @Override
                public long getSize() {
                    return 51 * 1024 * 1024L;
                }
            };
            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> service.upload(bigFile, "2026-01", "admin"));
            assertTrue(ex.getMessage().contains("Ukuran file melebihi batas"));
        }
    }

    @Nested
    @DisplayName("Periode Normalization & Validation")
    class PeriodeTests {

        @Test
        void normalizeRequestedPeriode_valid6Digits_returnsNormalized() {
            assertEquals("2026-01", service.normalizeRequestedPeriode("202601"));
            assertEquals("2026-12", service.normalizeRequestedPeriode("202612"));
        }

        @Test
        void normalizeRequestedPeriode_valid7Chars_returnsNormalized() {
            assertEquals("2026-01", service.normalizeRequestedPeriode("2026-01"));
            assertEquals("2026-11", service.normalizeRequestedPeriode("2026-11"));
        }

        @Test
        void normalizeRequestedPeriode_invalidFormats_throwsBadRequest() {
            assertThrows(BadRequestException.class, () -> service.normalizeRequestedPeriode(null));
            assertThrows(BadRequestException.class, () -> service.normalizeRequestedPeriode(""));
            assertThrows(BadRequestException.class, () -> service.normalizeRequestedPeriode("202613"));
            assertThrows(BadRequestException.class, () -> service.normalizeRequestedPeriode("2026-13"));
            assertThrows(BadRequestException.class, () -> service.normalizeRequestedPeriode("2026-1"));
            assertThrows(BadRequestException.class, () -> service.normalizeRequestedPeriode("2026"));
            assertThrows(BadRequestException.class, () -> service.normalizeRequestedPeriode("invalid"));
        }

        @Test
        void normalizeRowPeriode_variousInputs() {
            assertEquals("2026-01", service.normalizeRowPeriode("202601"));
            assertEquals("2026-01", service.normalizeRowPeriode("202601.0"));
            assertEquals("2026-01", service.normalizeRowPeriode("2026-01"));
            assertEquals("2026-01", service.normalizeRowPeriode("2026-01.0"));
            assertEquals("", service.normalizeRowPeriode(null));
            assertEquals("", service.normalizeRowPeriode(""));
        }
    }

    @Nested
    @DisplayName("NIPAM Normalization")
    class NipamNormalizationTests {

        @Test
        void normalizeNipam_standardNumeric_zeroPadsTo9Digits() {
            assertEquals("000012345", service.normalizeNipam("12345"));
            assertEquals("000012345", service.normalizeNipam("12345.0"));
            assertEquals("710100239", service.normalizeNipam("710100239"));
            assertEquals("710100239", service.normalizeNipam("710100239.0"));
        }

        @Test
        void normalizeNipam_alphanumeric_keepsAsIs() {
            assertEquals("nipam1", service.normalizeNipam("nipam1"));
        }

        @Test
        void normalizeNipam_nullOrBlank_returnsNull() {
            assertNull(service.normalizeNipam(null));
            assertNull(service.normalizeNipam("   "));
        }
    }

    @Nested
    @DisplayName("Upload Process & Business Validation")
    class UploadProcessTests {

        @Test
        void upload_successfulUpload_callsBatchUpsert() throws IOException {
            List<List<Object>> rows = List.of(
                    List.of("1", "202601", "710100239", "Pegawai 1", 5_000_000.0, 250_000.0),
                    List.of("2", "2026-01", "12345.0", "Pegawai 2", 4_500_000.0, 225_000.0)
            );
            MockMultipartFile file = createExcelFile("test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", rows);

            when(batchRepository.findExistingPegawaiNipams(any()))
                    .thenReturn(Set.of("710100239", "000012345"));
            when(batchRepository.batchUpsert(eq("2026-01"), any(), eq("admin")))
                    .thenReturn(new GajiKpiUploadResponse("2026-01", 2, 2, 0));

            GajiKpiUploadResponse result = service.upload(file, "202601", "admin");

            assertNotNull(result);
            assertEquals("2026-01", result.periode());
            assertEquals(2, result.totalRows());
            assertEquals(2, result.inserted());
            assertEquals(0, result.updated());

            verify(batchRepository).batchUpsert(eq("2026-01"), argThat(items -> {
                if (items.size() != 2) return false;
                GajiKpiItem item1 = items.get(0);
                GajiKpiItem item2 = items.get(1);
                return item1.nipam().equals("710100239") && item1.tunkin().equals(5_000_000.0)
                        && item2.nipam().equals("000012345") && item2.pph21Ter().equals(225_000.0);
            }), eq("admin"));
        }

        @Test
        void upload_emptyRowsExcel_throwsBadRequest() throws IOException {
            MockMultipartFile file = createExcelFile("test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", List.of());

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> service.upload(file, "2026-01", "admin"));
            assertTrue(ex.getMessage().contains("File Excel tidak memiliki data"));
        }

        @Test
        void upload_periodeMismatch_throwsBadRequest() throws IOException {
            List<List<Object>> rows = List.of(
                    List.of("1", "202602", "710100239", "Pegawai 1", 5_000_000.0, 250_000.0)
            );
            MockMultipartFile file = createExcelFile("test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", rows);

            when(batchRepository.findExistingPegawaiNipams(any()))
                    .thenReturn(Set.of("710100239"));

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> service.upload(file, "2026-01", "admin"));
            assertTrue(ex.getMessage().contains("Baris 6: Periode '2026-02' tidak sesuai dengan periode request '2026-01'"));
        }

        @Test
        void upload_duplicateNipamInFile_throwsBadRequest() throws IOException {
            List<List<Object>> rows = List.of(
                    List.of("1", "202601", "710100239", "Pegawai 1", 5_000_000.0, 250_000.0),
                    List.of("2", "202601", "710100239", "Pegawai 1 Copy", 5_000_000.0, 250_000.0)
            );
            MockMultipartFile file = createExcelFile("test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", rows);

            when(batchRepository.findExistingPegawaiNipams(any()))
                    .thenReturn(Set.of("710100239"));

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> service.upload(file, "2026-01", "admin"));
            assertTrue(ex.getMessage().contains("Baris 7: NIPAM '710100239' duplikat di dalam file"));
        }

        @Test
        void upload_nipamNotFoundInMasterPegawai_throwsBadRequest() throws IOException {
            List<List<Object>> rows = List.of(
                    List.of("1", "202601", "999999999", "Unknown Pegawai", 5_000_000.0, 250_000.0)
            );
            MockMultipartFile file = createExcelFile("test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", rows);

            when(batchRepository.findExistingPegawaiNipams(any()))
                    .thenReturn(Set.of());

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> service.upload(file, "2026-01", "admin"));
            assertTrue(ex.getMessage().contains("Baris 6: NIPAM '999999999' tidak terdaftar di sistem"));
        }

        @Test
        void upload_negativeTunkinAndPph_throwsBadRequest() throws IOException {
            List<List<Object>> rows = List.of(
                    List.of("1", "202601", "710100239", "Pegawai 1", -100.0, -50.0)
            );
            MockMultipartFile file = createExcelFile("test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", rows);

            when(batchRepository.findExistingPegawaiNipams(any()))
                    .thenReturn(Set.of("710100239"));

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> service.upload(file, "2026-01", "admin"));
            assertTrue(ex.getMessage().contains("Baris 6: Tunkin tidak boleh negatif"));
            assertTrue(ex.getMessage().contains("Baris 6: PPh 21 TER tidak boleh negatif"));
        }

        @Test
        void upload_moreThan20Errors_formatsSummary() throws IOException {
            List<List<Object>> rows = new ArrayList<>();
            for (int i = 0; i < 25; i++) {
                rows.add(List.of(String.valueOf(i + 1), "202601", "9000000" + (i < 10 ? "0" + i : i), "Pegawai " + i, -1.0, 0.0));
            }
            MockMultipartFile file = createExcelFile("test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", rows);

            when(batchRepository.findExistingPegawaiNipams(any()))
                    .thenReturn(Set.of());

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> service.upload(file, "2026-01", "admin"));
            assertTrue(ex.getMessage().contains("... dan "));
            assertTrue(ex.getMessage().contains("kesalahan lainnya"));
        }
    }

    @Nested
    @DisplayName("Template Download")
    class TemplateDownloadTests {

        @Test
        void getTemplateResource_templateExists_returnsResource() {
            Resource resource = service.getTemplateResource();
            assertNotNull(resource);
            assertTrue(resource.exists());
        }
    }
}
