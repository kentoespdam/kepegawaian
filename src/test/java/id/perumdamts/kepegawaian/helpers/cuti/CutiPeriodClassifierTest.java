package id.perumdamts.kepegawaian.helpers.cuti;

import id.perumdamts.kepegawaian.entities.commons.ECutiPeriod;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CutiPeriodClassifierTest {

    // ---------- classify: 5 bucket + boundary inklusif ----------

    @Test
    void classifyFullyNextYear() {
        assertEquals(ECutiPeriod.NEXT_YEAR,
                CutiPeriodClassifier.classify(LocalDate.of(2027, 1, 5), LocalDate.of(2027, 1, 9), 2026));
    }

    @Test
    void classifyOverlappingYear() {
        assertEquals(ECutiPeriod.OVERLAPPING,
                CutiPeriodClassifier.classify(LocalDate.of(2026, 12, 30), LocalDate.of(2027, 1, 3), 2026));
    }

    @Test
    void classifyJanJun() {
        assertEquals(ECutiPeriod.JAN_JUN,
                CutiPeriodClassifier.classify(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 5), 2026));
    }

    @Test
    void classifyJanJunBoundaryInclusiveAt30Jun() {
        assertEquals(ECutiPeriod.JAN_JUN,
                CutiPeriodClassifier.classify(LocalDate.of(2026, 6, 30), LocalDate.of(2026, 6, 30), 2026));
    }

    @Test
    void classifyJulDesBoundaryInclusiveAt1Jul() {
        assertEquals(ECutiPeriod.JUL_DES,
                CutiPeriodClassifier.classify(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 1), 2026));
    }

    @Test
    void classifyJulDes() {
        assertEquals(ECutiPeriod.JUL_DES,
                CutiPeriodClassifier.classify(LocalDate.of(2026, 12, 20), LocalDate.of(2026, 12, 31), 2026));
    }

    @Test
    void classifySpanningJunJul() {
        assertEquals(ECutiPeriod.JUN_JUL,
                CutiPeriodClassifier.classify(LocalDate.of(2026, 6, 28), LocalDate.of(2026, 7, 2), 2026));
    }

    @Test
    void classifySpanningJunJulCatchAllForSemesterCross() {
        assertEquals(ECutiPeriod.JUN_JUL,
                CutiPeriodClassifier.classify(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 9, 30), 2026));
    }

    // kepegawaian-ebt/kepegawaian-ciw regression: rentang menyeberang tahun dengan
    // refYear != startYear (dulu: IllegalArgumentException di klaim, silent no-op di
    // updateKuota) sekarang deterministik → OVERLAPPING.
    @Test
    void classifyPastYearSpanningDoesNotThrowAndIsOverlapping() {
        assertDoesNotThrow(() ->
                assertEquals(ECutiPeriod.OVERLAPPING,
                        CutiPeriodClassifier.classify(LocalDate.of(2025, 12, 29), LocalDate.of(2026, 1, 4), 2026)));
        assertEquals(ECutiPeriod.OVERLAPPING,
                CutiPeriodClassifier.classify(LocalDate.of(2025, 12, 29), LocalDate.of(2026, 1, 4), 2025));
    }

    // ---------- resolvePeriod: anchor ke createdAt (bukan now()) ----------

    @Test
    void resolvePeriodAnchorsOnCreatedAtYear() {
        LocalDateTime createdAt = LocalDateTime.of(2025, 12, 10, 9, 0);
        assertEquals(ECutiPeriod.OVERLAPPING,
                CutiPeriodClassifier.resolvePeriod(LocalDate.of(2025, 12, 30), LocalDate.of(2026, 1, 3), createdAt));
    }

    @Test
    void resolvePeriodWithNullCreatedAtFallsBackToStartYearMinusOne() {
        assertEquals(ECutiPeriod.NEXT_YEAR,
                CutiPeriodClassifier.resolvePeriod(LocalDate.of(2027, 1, 5), LocalDate.of(2027, 1, 9), null));
    }

    // ---------- deriveYearPair ----------

    @Test
    void nextYearPairUsesRefYearNotStartYearMinusOne() {
        CutiPeriodClassifier.YearPair pair = CutiPeriodClassifier.deriveYearPair(
                ECutiPeriod.NEXT_YEAR, LocalDate.of(2028, 1, 5), LocalDate.of(2028, 1, 9), 2026);
        assertEquals(2026, pair.year0());
        assertEquals(2028, pair.year1());
    }

    @Test
    void overlappingPairUsesCutiYears() {
        CutiPeriodClassifier.YearPair pair = CutiPeriodClassifier.deriveYearPair(
                ECutiPeriod.OVERLAPPING, LocalDate.of(2025, 12, 30), LocalDate.of(2026, 1, 3), 2025);
        assertEquals(2025, pair.year0());
        assertEquals(2026, pair.year1());
    }

    @Test
    void janJunPairUsesPrevAndCurrentYear() {
        CutiPeriodClassifier.YearPair pair = CutiPeriodClassifier.deriveYearPair(
                ECutiPeriod.JAN_JUN, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 5), 2026);
        assertEquals(2025, pair.year0());
        assertEquals(2026, pair.year1());
    }

    @Test
    void julDesPairUsesOnlyCurrentYear() {
        CutiPeriodClassifier.YearPair pair = CutiPeriodClassifier.deriveYearPair(
                ECutiPeriod.JUL_DES, LocalDate.of(2026, 12, 20), LocalDate.of(2026, 12, 31), 2026);
        assertEquals(2026, pair.year0());
        assertEquals(2026, pair.year1());
    }

    @Test
    void junJulPairUsesPrevAndCurrentYear() {
        CutiPeriodClassifier.YearPair pair = CutiPeriodClassifier.deriveYearPair(
                ECutiPeriod.JUN_JUL, LocalDate.of(2026, 6, 28), LocalDate.of(2026, 7, 2), 2026);
        assertEquals(2025, pair.year0());
        assertEquals(2026, pair.year1());
    }
}
