package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EFilterKenaikanBerkala;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EJenisKenaikanBerkala;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.KenaikanBerkalaResponse;
import id.perumdamts.kepegawaian.repositories.laporan.kepegawaian.KenaikanBerkalaRepository;
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
class KenaikanBerkalaServiceTest {

    @Mock private KenaikanBerkalaRepository repository;
    @InjectMocks private KenaikanBerkalaService service;

    private KenaikanBerkalaResponse sample(Boolean pendingGaji, Boolean pendingPangkat,
                                            LocalDate tglEksekusi) {
        return new KenaikanBerkalaResponse(
                1L, 10L, "8903001", "Budi",
                (byte) 0, "SK/2024/001",
                LocalDate.of(2024, 1, 15), LocalDate.of(2025, 1, 15),
                tglEksekusi,
                pendingGaji, pendingPangkat,
                "Kepala", LocalDate.of(2021, 6, 1),
                "IV/a", "Pembina", LocalDate.of(2020, 1, 1),
                4, 6, LocalDate.of(2015, 3, 10),
                11, 5, "S1-Teknik", "Bandung", LocalDate.of(1990, 1, 1)
        );
    }

    @Test
    void cleanupNullifiesEksekusiForNotPendingGaji_gajiBerkala() {
        // SK_KENAIKAN_GAJI_BERKALA + isPendingGaji=false → tanggalEksekusi should be null
        var raw = sample(false, false, LocalDate.of(2024, 6, 15));
        when(repository.fetch(EFilterKenaikanBerkala.BULAN_INI, EJenisKenaikanBerkala.SK_KENAIKAN_GAJI_BERKALA))
                .thenReturn(List.of(raw));

        var result = service.fetch(EFilterKenaikanBerkala.BULAN_INI, EJenisKenaikanBerkala.SK_KENAIKAN_GAJI_BERKALA);

        assertNull(result.getFirst().tanggalEksekusiSanksi(),
                "When isPendingGaji=false for GAJI_BERKALA, tanggalEksekusi should be null");
    }

    @Test
    void cleanupKeepsEksekusiForPendingGaji_gajiBerkala() {
        // SK_KENAIKAN_GAJI_BERKALA + isPendingGaji=true → tanggalEksekusi stays
        var raw = sample(true, false, LocalDate.of(2024, 6, 15));
        when(repository.fetch(EFilterKenaikanBerkala.BULAN_INI, EJenisKenaikanBerkala.SK_KENAIKAN_GAJI_BERKALA))
                .thenReturn(List.of(raw));

        var result = service.fetch(EFilterKenaikanBerkala.BULAN_INI, EJenisKenaikanBerkala.SK_KENAIKAN_GAJI_BERKALA);

        assertNotNull(result.getFirst().tanggalEksekusiSanksi());
        assertEquals(LocalDate.of(2024, 6, 15), result.getFirst().tanggalEksekusiSanksi());
    }

    @Test
    void cleanupNullifiesEksekusiForNotPendingPangkat_pangkatGol() {
        // SK_KENAIKAN_PANGKAT_GOLONGAN + isPendingPangkat=false → null
        var raw = sample(false, false, LocalDate.of(2024, 6, 15));
        when(repository.fetch(EFilterKenaikanBerkala.BULAN_INI, EJenisKenaikanBerkala.SK_KENAIKAN_PANGKAT_GOLONGAN))
                .thenReturn(List.of(raw));

        var result = service.fetch(EFilterKenaikanBerkala.BULAN_INI, EJenisKenaikanBerkala.SK_KENAIKAN_PANGKAT_GOLONGAN);

        assertNull(result.getFirst().tanggalEksekusiSanksi());
    }

    @Test
    void cleanupKeepsEksekusiForPendingPangkat_pangkatGol() {
        // SK_KENAIKAN_PANGKAT_GOLONGAN + isPendingPangkat=true → stays
        var raw = sample(false, true, LocalDate.of(2024, 6, 15));
        when(repository.fetch(EFilterKenaikanBerkala.BULAN_INI, EJenisKenaikanBerkala.SK_KENAIKAN_PANGKAT_GOLONGAN))
                .thenReturn(List.of(raw));

        var result = service.fetch(EFilterKenaikanBerkala.BULAN_INI, EJenisKenaikanBerkala.SK_KENAIKAN_PANGKAT_GOLONGAN);

        assertNotNull(result.getFirst().tanggalEksekusiSanksi());
    }

    @Test
    void cleanupNormalizesNegativeMkg() {
        var raw = new KenaikanBerkalaResponse(
                1L, 10L, "8903001", "Budi",
                (byte) 0, "SK/2024/001",
                LocalDate.of(2024, 1, 15), LocalDate.of(2025, 1, 15), null,
                false, false,
                "Kepala", LocalDate.of(2021, 6, 1),
                "IV/a", "Pembina", LocalDate.of(2020, 1, 1),
                -1, -5, LocalDate.of(2015, 3, 10),
                -2, -3, "S1-Teknik", "Bandung", LocalDate.of(1990, 1, 1)
        );
        when(repository.fetch(EFilterKenaikanBerkala.BULAN_INI, EJenisKenaikanBerkala.SK_KENAIKAN_GAJI_BERKALA))
                .thenReturn(List.of(raw));

        var result = service.fetch(EFilterKenaikanBerkala.BULAN_INI, EJenisKenaikanBerkala.SK_KENAIKAN_GAJI_BERKALA);

        assertEquals(0, result.getFirst().mkgTahun());
        assertEquals(0, result.getFirst().mkgBulan());
        assertEquals(0, result.getFirst().mkTahun());
        assertEquals(0, result.getFirst().mkBulan());
    }

    @Test
    void countDelegatesToRepository() {
        when(repository.count(EFilterKenaikanBerkala.GTE_1, EJenisKenaikanBerkala.SK_KENAIKAN_GAJI_BERKALA))
                .thenReturn(42L);
        var result = service.count(EFilterKenaikanBerkala.GTE_1, EJenisKenaikanBerkala.SK_KENAIKAN_GAJI_BERKALA);
        assertEquals(42L, result);
    }

    @Test
    void exportExcelReturnsResource() {
        when(repository.fetch(EFilterKenaikanBerkala.BULAN_INI, EJenisKenaikanBerkala.SK_KENAIKAN_GAJI_BERKALA))
                .thenReturn(List.of());
        var resource = service.exportExcel(EFilterKenaikanBerkala.BULAN_INI, EJenisKenaikanBerkala.SK_KENAIKAN_GAJI_BERKALA);
        assertNotNull(resource);
    }
}
