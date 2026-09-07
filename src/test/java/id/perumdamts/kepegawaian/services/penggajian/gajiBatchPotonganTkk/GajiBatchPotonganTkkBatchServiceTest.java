package id.perumdamts.kepegawaian.services.penggajian.gajiBatchPotonganTkk;

import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchPotonganTkk.GajiBatchPotonganTkkItem;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchPotonganTkk.GajiBatchPotonganTkkUploadResponse;
import id.perumdamts.kepegawaian.exceptions.BadRequestException;
import id.perumdamts.kepegawaian.repositories.penggajian.jooq.GajiBatchPotonganTkkBatchRepository;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GajiBatchPotonganTkkBatchServiceTest {

    @Mock
    private GajiBatchPotonganTkkBatchRepository batchRepository;

    private GajiBatchPotonganTkkBatchService service;

    @BeforeEach
    void setUp() {
        service = new GajiBatchPotonganTkkBatchService(batchRepository);
    }

    private InputStream createExcelInputStream(List<List<Object>> rows) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Sheet1");
            for (int r = 0; r < 4; r++) {
                Row headerRow = sheet.createRow(r);
                Cell c = headerRow.createCell(0);
                c.setCellValue("Header " + (r + 1));
            }
            if (rows != null) {
                for (int r = 0; r < rows.size(); r++) {
                    Row dataRow = sheet.createRow(4 + r);
                    List<Object> cellValues = rows.get(r);
                    if (cellValues != null) {
                        for (int c = 0; c < cellValues.size(); c++) {
                            Object val = cellValues.get(c);
                            if (val != null) {
                                Cell cell = dataRow.createCell(c);
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
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    @Nested
    @DisplayName("NIPAM Normalization")
    class NipamNormalizationTests {

        @Test
        void normalizeNipam_standardCases() {
            assertEquals("000001234", service.normalizeNipam("1234"));
            assertEquals("000001234", service.normalizeNipam("1234.0"));
            assertEquals("123456789", service.normalizeNipam("123456789"));
            assertEquals("000001234", service.normalizeNipam(" 1234.0 "));
            assertNull(service.normalizeNipam(null));
            assertNull(service.normalizeNipam(""));
            assertNull(service.normalizeNipam("   "));
        }
    }

    @Nested
    @DisplayName("Stream & Excel Validation")
    class StreamValidationTests {

        @Test
        void processStream_nullOrBlankBatchId_throwsBadRequest() throws IOException {
            InputStream in = createExcelInputStream(List.of());
            assertThrows(BadRequestException.class, () -> service.processStream(null, in));
            assertThrows(BadRequestException.class, () -> service.processStream("", in));
            assertThrows(BadRequestException.class, () -> service.processStream("   ", in));
        }

        @Test
        void processStream_nullInputStream_throwsBadRequest() {
            assertThrows(BadRequestException.class, () -> service.processStream("batch-1", null));
        }

        @Test
        void processStream_emptyRowsExcel_throwsBadRequest() throws IOException {
            InputStream in = createExcelInputStream(List.of());

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> service.processStream("batch-1", in));
            assertTrue(ex.getMessage().contains("File Excel tidak memiliki data"));
        }

        @Test
        void processStream_onlyBlankRows_throwsBadRequest() throws IOException {
            List<List<Object>> rows = new ArrayList<>();
            rows.add(null);
            rows.add(List.of("", "", "", ""));
            InputStream in = createExcelInputStream(rows);

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> service.processStream("batch-1", in));
            assertTrue(ex.getMessage().contains("File Excel tidak memiliki data"));
        }

        @Test
        void processStream_emptyNipamInRow_throwsBadRequest() throws IOException {
            List<List<Object>> rows = List.of(
                    List.of("1", "", "Budi", 50000.0)
            );
            InputStream in = createExcelInputStream(rows);

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> service.processStream("batch-1", in));
            assertTrue(ex.getMessage().contains("Baris 5: NIPAM tidak boleh kosong"));
        }

        @Test
        void processStream_duplicateNipamInFile_throwsBadRequest() throws IOException {
            List<List<Object>> rows = List.of(
                    List.of("1", "1234", "Budi", 50000.0),
                    List.of("2", "000001234", "Budi 2", 50000.0)
            );
            InputStream in = createExcelInputStream(rows);

            when(batchRepository.findExistingPegawaiNipams(any())).thenReturn(Set.of("000001234"));

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> service.processStream("batch-1", in));
            assertTrue(ex.getMessage().contains("Baris 6: NIPAM '000001234' duplikat di dalam file"));
        }

        @Test
        void processStream_nipamNotFoundInMasterPegawai_throwsBadRequest() throws IOException {
            List<List<Object>> rows = List.of(
                    List.of("1", "999999999", "Unknown", 50000.0)
            );
            InputStream in = createExcelInputStream(rows);

            when(batchRepository.findExistingPegawaiNipams(any())).thenReturn(Set.of());

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> service.processStream("batch-1", in));
            assertTrue(ex.getMessage().contains("Baris 5: NIPAM '999999999' tidak terdaftar di sistem"));
        }

        @Test
        void processStream_nullOrNegativePotongan_throwsBadRequest() throws IOException {
            List<List<Object>> rows = new ArrayList<>();
            rows.add(new ArrayList<>() {{
                add("1");
                add("1234");
                add("Budi");
                add(null);
            }});
            rows.add(List.of("2", "5678", "Siti", -1000.0));

            InputStream in = createExcelInputStream(rows);

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> service.processStream("batch-1", in));
            assertTrue(ex.getMessage().contains("Baris 5: Nilai potongan tidak boleh kosong"));
            assertTrue(ex.getMessage().contains("Baris 6: Nilai potongan tidak boleh negatif"));
        }

        @Test
        void processStream_moreThan20Errors_formatsSummary() throws IOException {
            List<List<Object>> rows = new ArrayList<>();
            for (int i = 0; i < 25; i++) {
                rows.add(List.of(String.valueOf(i + 1), "9000000" + (i < 10 ? "0" + i : i), "Pegawai " + i, -100.0));
            }
            InputStream in = createExcelInputStream(rows);

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> service.processStream("batch-1", in));
            assertTrue(ex.getMessage().contains("... dan "));
            assertTrue(ex.getMessage().contains("kesalahan lainnya"));
        }

        @Test
        void processStream_moreThan20MasterPegawaiErrors_formatsSummary() throws IOException {
            List<List<Object>> rows = new ArrayList<>();
            for (int i = 0; i < 25; i++) {
                rows.add(List.of(String.valueOf(i + 1), "9000000" + (i < 10 ? "0" + i : i), "Pegawai " + i, 50000.0));
            }
            InputStream in = createExcelInputStream(rows);

            when(batchRepository.findExistingPegawaiNipams(any())).thenReturn(Set.of());

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> service.processStream("batch-1", in));
            assertTrue(ex.getMessage().contains("... dan "));
            assertTrue(ex.getMessage().contains("kesalahan lainnya"));
        }

        @Test
        void processStream_success_invokesDeleteAndInsert() throws IOException {
            List<List<Object>> rows = List.of(
                    List.of("1", "1234.0", "Budi", 50000.0),
                    List.of("2", "5678", "Siti", 75000.4)
            );
            InputStream in = createExcelInputStream(rows);

            when(batchRepository.findExistingPegawaiNipams(any())).thenReturn(Set.of("000001234", "000005678"));
            when(batchRepository.deleteByBatchId("batch-123")).thenReturn(5);
            when(batchRepository.batchInsert(eq("batch-123"), any())).thenReturn(2);

            GajiBatchPotonganTkkUploadResponse response = service.processStream("batch-123", in);

            assertNotNull(response);
            assertEquals("batch-123", response.batchId());
            assertEquals(2, response.totalRows());
            assertEquals(2, response.totalInserted());

            verify(batchRepository).deleteByBatchId("batch-123");
            verify(batchRepository).batchInsert(eq("batch-123"), argThat(items -> {
                if (items.size() != 2) return false;
                GajiBatchPotonganTkkItem item1 = items.get(0);
                GajiBatchPotonganTkkItem item2 = items.get(1);
                return item1.nipam().equals("000001234") && item1.potongan().equals(50000)
                        && item2.nipam().equals("000005678") && item2.potongan().equals(75000);
            }));
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
