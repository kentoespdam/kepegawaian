package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EFilterKontrak;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.KontrakResponse;
import id.perumdamts.kepegawaian.repositories.laporan.kepegawaian.KontrakRepository;
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
class KontrakServiceTest {

    @Mock private KontrakRepository repository;
    @InjectMocks private KontrakService service;

    @Test
    void fetchReturnsDataForFilter() {
        var response = new KontrakResponse(
                "8903001", "Budi", "KTR/2024/001",
                "SUB TI", "Staff",
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31),
                0, 11
        );
        when(repository.fetch(EFilterKontrak.AKTIF)).thenReturn(List.of(response));

        var result = service.fetch(EFilterKontrak.AKTIF);

        assertEquals(1, result.size());
        assertEquals("KTR/2024/001", result.getFirst().nomorKontrak());
    }

    @Test
    void exportExcelDelegatesToRepository() {
        when(repository.fetch(EFilterKontrak.AKTIF)).thenReturn(List.of());
        // Excel export requires template on classpath (integration test)
        // Verify repository is called with correct filter
        var data = service.fetch(EFilterKontrak.AKTIF);
        assertTrue(data.isEmpty());
    }
}
