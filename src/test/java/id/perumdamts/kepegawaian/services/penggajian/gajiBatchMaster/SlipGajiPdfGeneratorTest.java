package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMaster;

import id.perumdamts.kepegawaian.config.SlipGajiProperties;
import id.perumdamts.kepegawaian.dto.penggajian.SlipGajiDto;
import id.perumdamts.kepegawaian.dto.penggajian.SlipGajiKomponenItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SlipGajiPdfGeneratorTest {

    private SlipGajiPdfGenerator pdfGenerator;

    @BeforeEach
    void setUp() {
        SlipGajiProperties properties = new SlipGajiProperties(
                new SlipGajiProperties.Kop(
                        "PERUMDAM TIRTA SATRIA",
                        "KABUPATEN BANYUMAS",
                        "Jl. Prof. Dr. Suharso No. 52 Purwokerto",
                        "(0281) 635831",
                        "(0281) 635831",
                        "www.tirtasatria.co.id",
                        "info@tirtasatria.co.id",
                        "images/logo-tirta-satria.png"
                )
        );
        pdfGenerator = new SlipGajiPdfGenerator(properties);
    }

    @Test
    void generatePdf_withCompleteData_returnsValidPdfBytes() {
        SlipGajiDto dto = new SlipGajiDto(
                1L,
                "202501-001",
                "2025-01",
                100L,
                "12345",
                "Bagus Pegawai",
                "Staf IT",
                "III/a",
                "Penata Muda",
                "- - -",
                List.of(new SlipGajiKomponenItemDto("GP", "Gaji Pokok", 3500000.0)),
                List.of(new SlipGajiKomponenItemDto("PPH", "Pph 21", 50000.0)),
                3500000.0,
                50000.0,
                3450000.0,
                0.0,
                3450000.0,
                List.of(new SlipGajiKomponenItemDto("ADD_TRANSPORT", "Transport Tambahan", 200000.0)),
                List.of(new SlipGajiKomponenItemDto("ADD_KOPERASI", "Simpanan Koperasi", 100000.0)),
                200000.0,
                100000.0,
                3550000.0
        );

        byte[] pdfBytes = pdfGenerator.generatePdf(dto);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
        String header = new String(pdfBytes, 0, 4);
        assertEquals("%PDF", header);
    }

    @Test
    void generatePdf_withoutAdditionalComponents_returnsValidPdfBytes() {
        SlipGajiDto dto = new SlipGajiDto(
                2L,
                "202501-001",
                "2025-01",
                101L,
                "67890",
                "Siti Pegawai",
                "Kasir",
                "II/c",
                "Pengatur",
                "- - -",
                List.of(new SlipGajiKomponenItemDto("GP", "Gaji Pokok", 3000000.0)),
                List.of(),
                3000000.0,
                0.0,
                3000000.0,
                0.0,
                3000000.0,
                List.of(),
                List.of(),
                0.0,
                0.0,
                3000000.0
        );

        byte[] pdfBytes = pdfGenerator.generatePdf(dto);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
        assertEquals("%PDF", new String(pdfBytes, 0, 4));
    }

    @Test
    void generatePdf_withAddInPotongan_movesToAdditionalComponents() {
        SlipGajiDto dto = new SlipGajiDto(
                3L,
                "202501-003",
                "2025-01",
                102L,
                "11223",
                "Ahmad Pegawai",
                "Kabag",
                "IV/a",
                "Pembina",
                "- - -",
                List.of(new SlipGajiKomponenItemDto("GP", "Gaji Pokok", 4000000.0)),
                List.of(
                        new SlipGajiKomponenItemDto("POT_PENSIUN", "Potongan Pensiun", 100000.0),
                        new SlipGajiKomponenItemDto("ADD_KOPERASI", "Simpanan Koperasi", 50000.0)
                ),
                4000000.0,
                150000.0,
                3850000.0,
                0.0,
                3850000.0,
                List.of(),
                List.of(),
                0.0,
                50000.0,
                3850000.0
        );

        byte[] pdfBytes = pdfGenerator.generatePdf(dto);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
        assertEquals("%PDF", new String(pdfBytes, 0, 4));

        List<SlipGajiKomponenItemDto> addPotongan = SlipGajiTableHelper.filterAdditionalPotongan(dto);
        assertEquals(1, addPotongan.size());
        assertEquals("ADD_KOPERASI", addPotongan.get(0).kode());

        List<SlipGajiKomponenItemDto> regularPotongan = SlipGajiTableHelper.filterRegularPotongan(dto);
        assertEquals(1, regularPotongan.size());
        assertEquals("POT_PENSIUN", regularPotongan.get(0).kode());
    }
}
