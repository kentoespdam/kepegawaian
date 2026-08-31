package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.DukResponse;
import id.perumdamts.kepegawaian.repositories.laporan.kepegawaian.DukRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
                (byte) 2
        );
        when(repository.fetch()).thenReturn(List.of(response));

        var result = service.fetch();

        assertEquals(1, result.size());
        assertEquals("Budi", result.getFirst().nama());
        assertEquals("8903001", result.getFirst().nipam());
    }

    @Test
    void exportExcelReturnsResource() {
        when(repository.fetch()).thenReturn(List.of());
        var resource = service.exportExcel();
        assertNotNull(resource);
    }
}
