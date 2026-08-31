package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.*;
import id.perumdamts.kepegawaian.repositories.laporan.kepegawaian.StatistikRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatistikServiceTest {

    @Mock private StatistikRepository repository;
    @InjectMocks private StatistikService service;

    @Test
    void fetchGolonganReturnsData() {
        when(repository.fetchByGolongan()).thenReturn(List.of(
                new StatistikGolonganResponse("IV/a", "Pembina", 10, 5, 15, 50.0)
        ));
        var result = service.fetchGolongan();
        assertEquals(1, result.size());
        assertEquals("IV/a", result.getFirst().golongan());
    }

    @Test
    void fetchPendidikan1ReturnsData() {
        when(repository.fetchByPendidikan1()).thenReturn(List.of(
                new StatistikPendidikan1Response("S1", 50, 70.0)
        ));
        var result = service.fetchPendidikan1();
        assertEquals(1, result.size());
    }

    @Test
    void fetchPendidikan2ReturnsData() {
        when(repository.fetchByPendidikan2(2024, 1)).thenReturn(List.of(
                new StatistikPendidikan2Response(1L, "S1", 5, 3, 2, 1, 0, 11, 2, 1, 0, 5, 8, 3, 2, 1, 6, 6, 5, 11)
        ));
        var result = service.fetchPendidikan2(2024, 1);
        assertEquals(1, result.size());
        assertEquals(11, result.getFirst().jmlGolongan());
    }

    @Test
    void fetchUmurRangeGroupsCorrectly() {
        var umurData = List.of(
                new StatistikUmurResponse(18, 5, 10.0),
                new StatistikUmurResponse(25, 10, 20.0),
                new StatistikUmurResponse(35, 15, 30.0),
                new StatistikUmurResponse(45, 10, 20.0),
                new StatistikUmurResponse(55, 5, 10.0),
                new StatistikUmurResponse(62, 5, 10.0)
        );
        when(repository.fetchByUmur()).thenReturn(umurData);

        var result = service.fetchUmurRange();

        assertEquals(6, result.size());
        assertEquals("<20", result.getFirst().range());
        assertEquals(5, result.getFirst().total());
        assertEquals("20-29", result.get(1).range());
        assertEquals(10, result.get(1).total());
        assertEquals("30-39", result.get(2).range());
        assertEquals(15, result.get(2).total());
        assertEquals(">60", result.getLast().range());
        assertEquals(5, result.getLast().total());
    }

    @Test
    void fetchJenisKelaminReturnsData() {
        when(repository.fetchByJenisKelamin()).thenReturn(List.of(
                new StatistikJenisKelaminResponse("Laki-laki", 30, 60.0),
                new StatistikJenisKelaminResponse("Perempuan", 20, 40.0)
        ));
        var result = service.fetchJenisKelamin();
        assertEquals(2, result.size());
    }

    @Test
    void fetchGelarReturnsData() {
        when(repository.fetchByGelar()).thenReturn(List.of(
                new StatistikGelarResponse("S1", "S.Kom.", 20, 40.0)
        ));
        var result = service.fetchGelar();
        assertEquals(1, result.size());
    }

    @Test
    void fetchAgamaReturnsData() {
        when(repository.fetchByAgama()).thenReturn(List.of(
                new StatistikAgamaResponse("Islam", 40, 80.0)
        ));
        var result = service.fetchAgama();
        assertEquals(1, result.size());
    }

    @Test
    void fetchStatusPegawaiReturnsData() {
        when(repository.fetchByStatusPegawai()).thenReturn(List.of(
                new StatistikStatusPegawaiResponse("Pegawai Tetap", 30, 60.0)
        ));
        var result = service.fetchStatusPegawai();
        assertEquals(1, result.size());
    }

    @Test
    void fetchUmurRangeSkipsNullUmur() {
        var umurData = List.of(
                new StatistikUmurResponse(null, 3, 10.0),
                new StatistikUmurResponse(25, 10, 20.0),
                new StatistikUmurResponse(45, 10, 20.0),
                new StatistikUmurResponse(62, 5, 10.0)
        );
        when(repository.fetchByUmur()).thenReturn(umurData);

        var result = service.fetchUmurRange();

        assertEquals(6, result.size());
        assertEquals(0, result.getFirst().total());  // <20: no non-null umur <20
        assertEquals(10, result.get(1).total());     // 20-29
        assertEquals(10, result.get(3).total());     // 40-49
        assertEquals(5, result.getLast().total());   // >60
        // grandTotal should exclude null umur entries
        assertEquals(25, result.stream().mapToInt(StatistikUmurRangeResponse::total).sum());
    }

    @Test
    void exportExcelPendidikan2ReturnsResource() {
        when(repository.fetchByPendidikan2(2024, 1)).thenReturn(List.of());
        var resource = service.exportExcelPendidikan2(2024, 1);
        assertNotNull(resource);
    }
}
