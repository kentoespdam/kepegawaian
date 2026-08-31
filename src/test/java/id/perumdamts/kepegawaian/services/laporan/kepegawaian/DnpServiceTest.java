package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.DnpResponse;
import id.perumdamts.kepegawaian.repositories.laporan.kepegawaian.DnpRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DnpServiceTest {

    @Mock private DnpRepository repository;
    @InjectMocks private DnpService service;

    private DnpResponse sampleDnp(String kode, Integer levelJabatan) {
        return new DnpResponse(
                kode, levelJabatan, "Budi", "8903001", "Kepala",
                "01.06.2021", "Pembina", "IV/a", "01.01.2020",
                4, 6, "10.03.2015", 11, 5,
                "S1 Teknik Informatika", "Bandung, 01.01.1990"
        );
    }

    @Test
    void cleanupMasksDireksiOrganisasi() {
        // level 2,3,4 → kode should be masked to "1"
        var data = List.of(sampleDnp("3", 2), sampleDnp("2", 3), sampleDnp("5", 4));
        when(repository.fetch()).thenReturn(data);

        var result = service.fetch();

        assertEquals("1", result.get(0).kodeOrganisasi());
        assertEquals("1", result.get(1).kodeOrganisasi());
        assertEquals("1", result.get(2).kodeOrganisasi());
    }

    @Test
    void cleanupKeepsNonDireksiKode() {
        // level 5,6 → kode stays as-is
        var data = List.of(sampleDnp("3", 5), sampleDnp("5", 6));
        when(repository.fetch()).thenReturn(data);

        var result = service.fetch();

        assertEquals("3", result.get(0).kodeOrganisasi());
        assertEquals("5", result.get(1).kodeOrganisasi());
    }

    @Test
    void fetchOrganisasiReturnsDireksiFirst() {
        when(repository.fetchOrganisasiCodes(4)).thenReturn(List.of(
                Map.of("kode", "3", "nama", "SUB TI")
        ));

        var result = service.fetchOrganisasi();

        assertEquals(2, result.size());
        assertEquals("DIREKSI", result.getFirst().get("nama"));
        assertEquals("3", result.get(1).get("kode"));
    }
}
