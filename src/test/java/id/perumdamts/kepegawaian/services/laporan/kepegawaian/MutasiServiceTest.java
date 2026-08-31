package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.MutasiResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import id.perumdamts.kepegawaian.repositories.laporan.kepegawaian.MutasiRepository;
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
class MutasiServiceTest {

    @Mock private MutasiRepository repository;
    @InjectMocks private MutasiService service;

    @Test
    void fetchReturnsFilteredData() {
        var response = new MutasiResponse(
                EJenisMutasi.MUTASI_LOKER, "8903001", "Andi",
                LocalDate.of(2024, 1, 15), "SUB TI", "Kepala",
                "IV/a", "SUB Keuangan", "Manager", "III/b", "Mutasi lokasi"
        );
        when(repository.fetch(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), null))
                .thenReturn(List.of(response));

        var result = service.fetch(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), null);

        assertEquals(1, result.size());
        assertEquals(EJenisMutasi.MUTASI_LOKER, result.getFirst().jenisMutasi());
    }

    @Test
    void exportExcelReturnsResource() {
        when(repository.fetch(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), null))
                .thenReturn(List.of());
        var resource = service.exportExcel(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), null);
        assertNotNull(resource);
    }
}
