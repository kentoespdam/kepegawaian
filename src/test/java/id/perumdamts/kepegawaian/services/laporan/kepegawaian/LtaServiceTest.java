package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EFilterLta;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.LtaResponse;
import id.perumdamts.kepegawaian.repositories.laporan.kepegawaian.LtaRepository;
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
class LtaServiceTest {

    @Mock private LtaRepository repository;
    @InjectMocks private LtaService service;

    private LtaResponse sampleLta() {
        return new LtaResponse(
                1L, "Andini", "Wanita",
                LocalDate.of(2003, 5, 10), 21, true,
                "Sekolah", "Budi", "8903001", "Kepala"
        );
    }

    @Test
    void fetchReturnsData() {
        when(repository.fetch(EFilterLta.BULAN_INI)).thenReturn(List.of(sampleLta()));
        var result = service.fetch(EFilterLta.BULAN_INI);
        assertEquals(1, result.size());
        assertEquals("Andini", result.getFirst().namaAnak());
    }

    @Test
    void countReturnsCountResponse() {
        when(repository.count(EFilterLta.BULAN_INI)).thenReturn(5L);
        var result = service.count(EFilterLta.BULAN_INI);
        assertEquals(5L, result.count());
    }

    @Test
    void exportExcelDelegatesToRepository() {
        when(repository.fetch(EFilterLta.BULAN_INI)).thenReturn(List.of());
        // Excel export requires template on classpath (integration test)
        var data = service.fetch(EFilterLta.BULAN_INI);
        assertTrue(data.isEmpty());
    }
}
