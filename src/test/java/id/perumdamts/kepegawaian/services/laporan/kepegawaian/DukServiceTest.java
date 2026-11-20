package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.DukResponse;
import id.perumdamts.kepegawaian.repositories.laporan.kepegawaian.DukRepository;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DukServiceTest {

    @Mock private DukRepository repository;
    @InjectMocks private DukService service;

    @Test
    void fetchReturnsDataFromRepository() {
        var response = new DukResponse(
                "Budi", "8903001", "IV/a", "Pembina",
                LocalDate.of(2020, 1, 15), "Kepala Seksi",
                LocalDate.of(2021, 6, 1), LocalDate.of(2015, 3, 10),
                11, 5, 30, "Teknik Informatika", 2015, "S1",
                "Pegawai Tetap"
        );
        when(repository.fetch()).thenReturn(List.of(response));

        var result = service.fetch();

        assertEquals(1, result.size());
        assertEquals("Budi", result.getFirst().nama());
        assertEquals("8903001", result.getFirst().nipam());
        assertEquals("Pegawai Tetap", result.getFirst().statusPegawai());
    }

    @Test
    void exportExcelReturnsResource() {
        when(repository.fetch()).thenReturn(List.of());
        var resource = service.exportExcel();
        assertNotNull(resource);
    }

    @Test
    void exportExcelWritesFormattedDatesAndStatus() throws IOException {
        var response = new DukResponse(
                "Budi", "8903001", "IV/a", "Pembina",
                LocalDate.of(2020, 1, 15), "Kepala Seksi",
                LocalDate.of(2021, 6, 1), LocalDate.of(2015, 3, 10),
                11, 5, 30, "Teknik Informatika", 2015, "S1",
                "Pegawai Tetap"
        );
        when(repository.fetch()).thenReturn(List.of(response));
        var resource = service.exportExcel();
        assertNotNull(resource);

        try (var wb = new XSSFWorkbook(resource.getInputStream())) {
            var sheet = wb.getSheetAt(0);
            var row = sheet.getRow(6);
            assertNotNull(row);
            assertEquals("15.01.2020", row.getCell(5).getStringCellValue());
            assertEquals("01.06.2021", row.getCell(7).getStringCellValue());
            assertEquals("10.03.2015", row.getCell(8).getStringCellValue());
            assertEquals("Pegawai Tetap", row.getCell(15).getStringCellValue());
        }
    }
}
