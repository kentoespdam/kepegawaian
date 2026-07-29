package id.perumdamts.kepegawaian.services.penggajian.detailDasarGaji;

import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiNominal;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jpa.GolonganRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jooq.DetailDasarGajiQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link DetailDasarGajiQueryService#findNominalByGolonganAndMasaKerja(Long, Integer)}.
 * Mocks {@link GolonganRepository} and {@link DetailDasarGajiQueryRepository} — no database required.
 */
@ExtendWith(MockitoExtension.class)
class DetailDasarGajiQueryServiceTest {

    private static final Long GOLONGAN_ID = 1L;
    private static final Integer MASA_KERJA = 5;
    private static final Integer GOLONGAN_KODE = 2;
    private static final Double NOMINAL = 3_500_000.0;

    @Mock
    private GolonganRepository golonganRepository;
    @Mock
    private DetailDasarGajiQueryRepository queryRepository;

    private DetailDasarGajiQueryService service;

    @BeforeEach
    void setUp() {
        service = new DetailDasarGajiQueryService(queryRepository, golonganRepository);
    }

    @Test
    void findNominalByGolonganAndMasaKerja_returnsNominal() {
        Golongan golongan = new Golongan(GOLONGAN_ID, "I." + GOLONGAN_KODE, "Pangkat");
        DetailDasarGajiNominal expected = new DetailDasarGajiNominal(NOMINAL);

        when(golonganRepository.findById(GOLONGAN_ID)).thenReturn(Optional.of(golongan));
        when(queryRepository.getNominalByGolonganAndMasaKerja(GOLONGAN_KODE, MASA_KERJA))
                .thenReturn(Optional.of(expected));

        DetailDasarGajiNominal result = service.findNominalByGolonganAndMasaKerja(GOLONGAN_ID, MASA_KERJA);

        assertSame(expected, result);
        assertEquals(NOMINAL, result.nominal());
        verify(golonganRepository).findById(GOLONGAN_ID);
        verify(queryRepository).getNominalByGolonganAndMasaKerja(GOLONGAN_KODE, MASA_KERJA);
    }

    @Test
    void findNominalByGolonganAndMasaKerja_throwsNotFound_whenGolonganNotExists() {
        when(golonganRepository.findById(GOLONGAN_ID)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.findNominalByGolonganAndMasaKerja(GOLONGAN_ID, MASA_KERJA));

        assertTrue(ex.getMessage().contains("Golongan not found"),
                "Message must mention 'Golongan not found', got: " + ex.getMessage());
        assertTrue(ex.getMessage().contains(String.valueOf(GOLONGAN_ID)),
                "Message must contain golonganId, got: " + ex.getMessage());
        verify(golonganRepository).findById(GOLONGAN_ID);
        verifyNoInteractions(queryRepository);
    }

    @Test
    void findNominalByGolonganAndMasaKerja_throwsNotFound_whenDetailNotExists() {
        Golongan golongan = new Golongan(GOLONGAN_ID, "I." + GOLONGAN_KODE, "Pangkat");

        when(golonganRepository.findById(GOLONGAN_ID)).thenReturn(Optional.of(golongan));
        when(queryRepository.getNominalByGolonganAndMasaKerja(GOLONGAN_KODE, MASA_KERJA))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.findNominalByGolonganAndMasaKerja(GOLONGAN_ID, MASA_KERJA));

        assertTrue(ex.getMessage().contains("Detail Dasar Gaji not found"),
                "Message must mention 'Detail Dasar Gaji not found', got: " + ex.getMessage());
        verify(golonganRepository).findById(GOLONGAN_ID);
        verify(queryRepository).getNominalByGolonganAndMasaKerja(GOLONGAN_KODE, MASA_KERJA);
    }
}
