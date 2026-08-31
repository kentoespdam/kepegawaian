package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies ExcelDateHelper matches Python's format_date_vectorized output.
 * Python: "dd MonthName yyyy" with Indonesian month names.
 */
class ExcelDateHelperTest {

    @Test
    void formatDateMatchesPythonOutput() {
        // Python format_date_vectorized(LocalDate(2024, 1, 15)) → "15 Januari 2024"
        assertEquals("15 Januari 2024", ExcelDateHelper.formatDate(LocalDate.of(2024, 1, 15)));
        assertEquals("1 Maret 2024", ExcelDateHelper.formatDate(LocalDate.of(2024, 3, 1)));
        assertEquals("31 Desember 2023", ExcelDateHelper.formatDate(LocalDate.of(2023, 12, 31)));
        assertEquals("29 Februari 2024", ExcelDateHelper.formatDate(LocalDate.of(2024, 2, 29)));
    }

    @Test
    void formatDateNullReturnsNull() {
        assertNull(ExcelDateHelper.formatDate(null));
    }

    @Test
    void mutasiTitleMatchesPython() {
        // Python Mutasi: "Periode: 01-Januari-2024 s/d 31-Desember-2024"
        var result = ExcelDateHelper.mutasiTitle(
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        assertTrue(result.contains("1-Januari-2024"));
        assertTrue(result.contains("31-Desember-2024"));
    }

    @Test
    void bulanTitleMatchesPython() {
        // Python DUK/DNP: "BULAN : Agustus 2026"
        var result = ExcelDateHelper.bulanTitle();
        assertTrue(result.startsWith("BULAN : "));
        assertTrue(result.contains("2026"));
    }

    @Test
    void bulanColonTitleMatchesPython() {
        // Python LTA/KB: "Bulan: Agustus 2026"
        var result = ExcelDateHelper.bulanColonTitle();
        assertTrue(result.startsWith("Bulan: "));
        assertTrue(result.contains("2026"));
    }
}
