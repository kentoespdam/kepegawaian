package id.perumdamts.kepegawaian.services.penggajian.gajiBatchPotonganTambahan;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchPotonganTambahan.GajiBatchPotonganTambahanItem;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchPotonganTambahan.GajiBatchPotonganTambahanUploadResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.exceptions.BadRequestException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchRootLampiranRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jooq.GajiBatchPotonganTambahanBatchRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchMasterRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchRootRepository;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesCommandService;
import id.perumdamts.kepegawaian.utils.FileUploadUtil;
import id.perumdamts.kepegawaian.utils.UploadResultUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GajiBatchPotonganTambahanBatchServiceTest {

    @Mock
    private GajiBatchRootRepository rootRepository;
    @Mock
    private GajiBatchMasterRepository masterRepository;
    @Mock
    private GajiBatchRootLampiranRepository lampiranRepository;
    @Mock
    private GajiBatchPotonganTambahanBatchRepository tambahanBatchRepository;
    @Mock
    private GajiBatchMasterProsesCommandService prosesCommandService;
    @Mock
    private FileUploadUtil fileUploadUtil;
    @Mock
    private GajiBatchPotonganTambahanTemplateGenerator templateGenerator;

    private GajiBatchPotonganTambahanBatchService service;

    @BeforeEach
    void setUp() {
        service = new GajiBatchPotonganTambahanBatchService(
                rootRepository, masterRepository, lampiranRepository,
                tambahanBatchRepository, prosesCommandService, fileUploadUtil, templateGenerator);
    }

    // --- helpers: workbook layout mengikuti form resmi (anchor baris 9, label baris 10, data baris 11+) ---

    private XSSFWorkbook workbook() {
        return new XSSFWorkbook();
    }

    private void addSheet(XSSFWorkbook wb, String sheetName, List<String> labels, List<List<Object>> rows) {
        Sheet sheet = wb.createSheet(sheetName);
        Row header = sheet.createRow(8);
        header.createCell(1).setCellValue("NAMA");
        header.createCell(2).setCellValue("NIPAM");
        Row label = sheet.createRow(9);
        for (int i = 0; i < labels.size(); i++) {
            label.createCell(4 + i).setCellValue(labels.get(i));
        }
        int r = 10;
        for (List<Object> row : rows) {
            Row data = sheet.createRow(r++);
            for (int c = 0; c < row.size(); c++) {
                Object v = row.get(c);
                Cell cell = data.createCell(c);
                if (v instanceof Number n) {
                    cell.setCellValue(n.doubleValue());
                } else if (v != null) {
                    cell.setCellValue(v.toString());
                }
            }
        }
    }

    private ByteArrayInputStream toStream(XSSFWorkbook wb) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

    private GajiBatchRoot root(String id, EProsesGaji status) {
        GajiBatchRoot r = new GajiBatchRoot();
        r.setId(id);
        r.setStatus(status);
        return r;
    }

    private GajiBatchMaster master(Long id, String nipam) {
        GajiBatchMaster m = new GajiBatchMaster();
        m.setId(id);
        m.setNipam(nipam);
        return m;
    }

    @SuppressWarnings("unchecked")
    private List<GajiBatchPotonganTambahanItem> capturedItems() {
        ArgumentCaptor<List<GajiBatchPotonganTambahanItem>> captor = ArgumentCaptor.forClass(List.class);
        verify(tambahanBatchRepository).batchInsert(any(), captor.capture());
        return captor.getValue();
    }

    // --- T1 · parser core ---

    @Nested
    @DisplayName("T1 · Parsing core")
    class ParserCoreTests {

        @Test
        void multiSheetParsesAllLabelsWithSlugCodesAndNormalizedNipam() throws Exception {
            XSSFWorkbook wb = workbook();
            addSheet(wb, "DIREKSI", List.of("PINJ.KOPR.", "SIMPANAN KOPERASI"),
                    List.of(
                            List.of("1", "Budi", "1234", 5000000.0, 100000.0, 50000.0),
                            List.of("2", "Siti", "5678", 4000000.0, 0.0, 20000.0)));
            addSheet(wb, "BAGIAN_KEUANGAN", List.of("Gn. SIMPING"),
                    List.of(List.of("1", "Ani", "9999", 4000000.0, 75000.0)));

            when(rootRepository.findById("root-001")).thenReturn(Optional.of(root("root-001", EProsesGaji.WAIT_VERIFICATION_PHASE_2)));
            when(masterRepository.findByGajiBatchRoot_Id("root-001")).thenReturn(List.of(master(100L, "000001234"), master(101L, "000005678"), master(102L, "000009999")));
            when(tambahanBatchRepository.findMasterIdsWithAddRows("root-001")).thenReturn(Set.of());
            when(tambahanBatchRepository.deleteByRootBatchId("root-001")).thenReturn(0);
            when(tambahanBatchRepository.batchInsert(eq("root-001"), any())).thenAnswer(inv -> ((List<?>) inv.getArgument(1)).size());

            GajiBatchPotonganTambahanUploadResponse resp = service.process(toStream(wb), "root-001");

            assertEquals(4, resp.totalRows());
            List<GajiBatchPotonganTambahanItem> items = capturedItems();
            assertEquals(4, items.size());
            assertEquals("000001234", items.getFirst().nipam());
            assertEquals(Set.of("ADD_PINJ_KOPR", "ADD_SIMPANAN_KOPERASI", "ADD_GN_SIMPING"),
                    items.stream().map(GajiBatchPotonganTambahanItem::kode).collect(Collectors.toSet()));
            assertEquals("PINJ.KOPR.", items.getFirst().nama());
            assertEquals(100000.0, items.getFirst().nilai());
            assertEquals(99, items.getFirst().urut());
            assertEquals(EJenisGaji.POTONGAN, items.getFirst().lokalJenisGaji());
            assertEquals("DIREKSI", items.getFirst().sheetName());
            assertTrue(items.stream().noneMatch(i -> i.nilai() == 0.0), "nilai 0 harus di-skip");
        }

        @Test
        void sameNipamMultipleCategoriesInOneSheetIsAllowed() throws Exception {
            XSSFWorkbook wb = workbook();
            addSheet(wb, "S1", List.of("POT1", "POT2"),
                    List.of(List.of("1", "Budi", "1234", 1000.0, 100.0, 50.0)));

            when(rootRepository.findById("root-011")).thenReturn(Optional.of(root("root-011", EProsesGaji.WAIT_VERIFICATION_PHASE_2)));
            when(masterRepository.findByGajiBatchRoot_Id("root-011")).thenReturn(List.of(master(100L, "000001234")));
            when(tambahanBatchRepository.findMasterIdsWithAddRows("root-011")).thenReturn(Set.of());
            when(tambahanBatchRepository.deleteByRootBatchId("root-011")).thenReturn(0);
            when(tambahanBatchRepository.batchInsert(eq("root-011"), any())).thenAnswer(inv -> ((List<?>) inv.getArgument(1)).size());

            GajiBatchPotonganTambahanUploadResponse resp = service.process(toStream(wb), "root-011");
            assertEquals(2, resp.totalRows());
        }

        @Test
        void numericNipamWithDotZeroIsNormalized() throws Exception {
            XSSFWorkbook wb = workbook();
            addSheet(wb, "S1", List.of("POT1"),
                    List.of(List.of("1", "Budi", "1234.0", 1000.0, 100.0)));

            when(rootRepository.findById("root-012")).thenReturn(Optional.of(root("root-012", EProsesGaji.WAIT_VERIFICATION_PHASE_2)));
            when(masterRepository.findByGajiBatchRoot_Id("root-012")).thenReturn(List.of(master(100L, "000001234")));
            when(tambahanBatchRepository.findMasterIdsWithAddRows("root-012")).thenReturn(Set.of());
            when(tambahanBatchRepository.deleteByRootBatchId("root-012")).thenReturn(0);
            when(tambahanBatchRepository.batchInsert(eq("root-012"), any())).thenAnswer(inv -> ((List<?>) inv.getArgument(1)).size());

            service.process(toStream(wb), "root-012");
            assertEquals("000001234", capturedItems().getFirst().nipam());
        }

        @Test
        void totalRowWithEmptyNamaNipamIsSkipped() throws Exception {
            XSSFWorkbook wb = workbook();
            addSheet(wb, "S1", List.of("POT1"),
                    List.of(
                            List.of("1", "Budi", "1234", 1000.0, 100.0),
                            List.of("TOTAL", "", "", 1000.0, 100.0)));

            when(rootRepository.findById("root-013")).thenReturn(Optional.of(root("root-013", EProsesGaji.WAIT_VERIFICATION_PHASE_2)));
            when(masterRepository.findByGajiBatchRoot_Id("root-013")).thenReturn(List.of(master(100L, "000001234")));
            when(tambahanBatchRepository.findMasterIdsWithAddRows("root-013")).thenReturn(Set.of());
            when(tambahanBatchRepository.deleteByRootBatchId("root-013")).thenReturn(0);
            when(tambahanBatchRepository.batchInsert(eq("root-013"), any())).thenAnswer(inv -> ((List<?>) inv.getArgument(1)).size());

            GajiBatchPotonganTambahanUploadResponse resp = service.process(toStream(wb), "root-013");
            assertEquals(1, resp.totalRows());
        }
    }

    // --- T2 · validasi & guard ---

    @Nested
    @DisplayName("T2 · Validasi & guard")
    class GuardAndValidationTests {

        @Test
        void typoHeaderOnOneSheetRejectsWholeFile() throws Exception {
            XSSFWorkbook wb = workbook();
            addSheet(wb, "S1", List.of("POT1"), List.of(List.of("1", "Budi", "1234", 1000.0, 100.0)));
            Sheet s2 = wb.createSheet("S2");
            Row h = s2.createRow(8);
            h.createCell(1).setCellValue("NAMA");
            h.createCell(2).setCellValue("NIPAMX"); // typo

            when(rootRepository.findById("root-002")).thenReturn(Optional.of(root("root-002", EProsesGaji.WAIT_VERIFICATION_PHASE_2)));

            BadRequestException ex = assertThrows(BadRequestException.class, () -> service.process(toStream(wb), "root-002"));
            assertTrue(ex.getMessage().contains("tidak menemukan header"));
        }

        @Test
        void duplicateNipamAcrossSheetsThrowsBadRequest() throws Exception {
            XSSFWorkbook wb = workbook();
            addSheet(wb, "S1", List.of("POT1"), List.of(List.of("1", "Budi", "1234", 1000.0, 100.0)));
            addSheet(wb, "S2", List.of("POT2"), List.of(List.of("1", "Budi", "1234", 2000.0, 50.0)));

            when(rootRepository.findById("root-003")).thenReturn(Optional.of(root("root-003", EProsesGaji.WAIT_VERIFICATION_PHASE_2)));

            BadRequestException ex = assertThrows(BadRequestException.class, () -> service.process(toStream(wb), "root-003"));
            assertTrue(ex.getMessage().contains("duplikat antar sheet"));
        }

        @Test
        void negativeValueThrowsBadRequest() throws Exception {
            XSSFWorkbook wb = workbook();
            addSheet(wb, "S1", List.of("POT1"), List.of(List.of("1", "Budi", "1234", 1000.0, -50.0)));

            when(rootRepository.findById("root-004")).thenReturn(Optional.of(root("root-004", EProsesGaji.WAIT_VERIFICATION_PHASE_2)));

            BadRequestException ex = assertThrows(BadRequestException.class, () -> service.process(toStream(wb), "root-004"));
            assertTrue(ex.getMessage().contains("negatif"));
        }

        @Test
        void onlyZeroValuesThrowsNoData() throws Exception {
            XSSFWorkbook wb = workbook();
            addSheet(wb, "S1", List.of("POT1"), List.of(List.of("1", "Budi", "1234", 1000.0, 0.0)));

            when(rootRepository.findById("root-005")).thenReturn(Optional.of(root("root-005", EProsesGaji.WAIT_VERIFICATION_PHASE_2)));

            BadRequestException ex = assertThrows(BadRequestException.class, () -> service.process(toStream(wb), "root-005"));
            assertTrue(ex.getMessage().contains("tidak memiliki data"));
        }

        @Test
        void nipamNotFoundInBatchThrowsBadRequest() throws Exception {
            XSSFWorkbook wb = workbook();
            addSheet(wb, "S1", List.of("POT1"), List.of(List.of("1", "Budi", "999999999", 1000.0, 100.0)));

            when(rootRepository.findById("root-006")).thenReturn(Optional.of(root("root-006", EProsesGaji.WAIT_VERIFICATION_PHASE_2)));
            when(masterRepository.findByGajiBatchRoot_Id("root-006")).thenReturn(List.of(master(100L, "000001234")));

            BadRequestException ex = assertThrows(BadRequestException.class, () -> service.process(toStream(wb), "root-006"));
            assertTrue(ex.getMessage().contains("tidak ada di batch ini"));
        }

        @Test
        void invalidStatusThrowsBadRequest() throws Exception {
            XSSFWorkbook wb = workbook();
            addSheet(wb, "S1", List.of("POT1"), List.of(List.of("1", "Budi", "1234", 1000.0, 100.0)));

            when(rootRepository.findById("root-007")).thenReturn(Optional.of(root("root-007", EProsesGaji.PENDING)));

            BadRequestException ex = assertThrows(BadRequestException.class, () -> service.process(toStream(wb), "root-007"));
            assertTrue(ex.getMessage().contains("WAIT_VERIFICATION_PHASE_2"));
        }

        @Test
        void unknownRootThrowsNotFound() {
            when(rootRepository.findById("root-999")).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> service.process(new ByteArrayInputStream(new byte[0]), "root-999"));
        }

        @Test
        void emptyWorkbookThrowsNoData() {
            when(rootRepository.findById("root-010")).thenReturn(Optional.of(root("root-010", EProsesGaji.WAIT_VERIFICATION_PHASE_2)));
            BadRequestException ex = assertThrows(BadRequestException.class, () -> service.process(toStream(workbook()), "root-010"));
            assertTrue(ex.getMessage().contains("tidak memiliki data"));
        }
    }

    // --- T3 · replace & recalculate ---

    @Nested
    @DisplayName("T3 · Replace & recalculate")
    class ReplaceAndRecalculateTests {

        @Test
        void deleteExistingAddRowsThenInsertAndRecalcAffectedOnly() throws Exception {
            XSSFWorkbook wb = workbook();
            addSheet(wb, "S1", List.of("POT1"), List.of(List.of("1", "Budi", "1234", 1000.0, 100.0)));

            GajiBatchMaster m1 = master(100L, "000001234");
            GajiBatchMaster m2 = master(101L, "000005678");
            GajiBatchMaster m3 = master(102L, "000009999");
            when(rootRepository.findById("root-008")).thenReturn(Optional.of(root("root-008", EProsesGaji.WAIT_VERIFICATION_PHASE_2)));
            when(masterRepository.findByGajiBatchRoot_Id("root-008")).thenReturn(List.of(m1, m2, m3));
            // m2 punya baris ADD_% lama yang akan terhapus -> harus ikut di-recalculate walau tidak ada di file
            when(tambahanBatchRepository.findMasterIdsWithAddRows("root-008")).thenReturn(Set.of(101L));
            when(tambahanBatchRepository.deleteByRootBatchId("root-008")).thenReturn(2);
            when(tambahanBatchRepository.batchInsert(eq("root-008"), any())).thenAnswer(inv -> ((List<?>) inv.getArgument(1)).size());

            GajiBatchPotonganTambahanUploadResponse resp = service.process(toStream(wb), "root-008");

            assertEquals(1, resp.totalRows());
            verify(tambahanBatchRepository).deleteByRootBatchId("root-008");
            verify(tambahanBatchRepository).batchInsert(eq("root-008"), any());
            // terdampak: m1 (ada di file) + m2 (punya ADD_% lama); m3 tidak disentuh
            verify(prosesCommandService).recalculateAdditional(m1);
            verify(prosesCommandService).recalculateAdditional(m2);
            verify(prosesCommandService, never()).recalculateAdditional(m3);
        }

        @Test
        void uploadReturnsNSuccessAndSavesLampiran() throws Exception {
            XSSFWorkbook wb = workbook();
            addSheet(wb, "S1", List.of("POT1", "POT2"),
                    List.of(List.of("1", "Budi", "1234", 1000.0, 100.0, 50.0)));

            MultipartFile file = mock(MultipartFile.class);
            when(file.getInputStream()).thenReturn(toStream(wb));
            when(fileUploadUtil.uploadPenggajian(eq(file), anyString()))
                    .thenReturn(UploadResultUtil.build(true, "ok", "data.xlsx", "xlsx",
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "hash"));
            when(rootRepository.findById("root-009")).thenReturn(Optional.of(root("root-009", EProsesGaji.WAIT_VERIFICATION_PHASE_2)));
            when(masterRepository.findByGajiBatchRoot_Id("root-009")).thenReturn(List.of(master(100L, "000001234")));
            when(tambahanBatchRepository.findMasterIdsWithAddRows("root-009")).thenReturn(Set.of());
            when(tambahanBatchRepository.deleteByRootBatchId("root-009")).thenReturn(0);
            when(tambahanBatchRepository.batchInsert(eq("root-009"), any())).thenAnswer(inv -> ((List<?>) inv.getArgument(1)).size());

            SavedStatus<String> result = service.upload(file, "root-009");

            assertEquals(ESaveStatus.SUCCESS, result.getStatus());
            assertEquals("2 success", result.getData());
            verify(lampiranRepository).save(any());
        }
    }
}