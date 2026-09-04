package id.perumdamts.kepegawaian.utils;

import id.perumdamts.kepegawaian.exceptions.GajiFormulaException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GajiFormulaEvaluatorTest {
    private final GajiFormulaEvaluator evaluator = new GajiFormulaEvaluator();

    @Test
    void evaluate_arithmeticWithoutVars() {
        assertEquals(14.0, evaluator.evaluate("2 + 3 * 4", Map.of()));
    }

    @Test
    void evaluate_withComponentVars() {
        double result = evaluator.evaluate("GP + TUNJ_KINERJA - 100", Map.of(
                "GP", 2_000_000.0,
                "TUNJ_KINERJA", 500_000.0));
        assertEquals(2_499_900.0, result);
    }

    @Test
    void evaluate_ceilRoundsUp() {
        assertEquals(3.0, evaluator.evaluate("ceil(2.1)", Map.of()));
        assertEquals(3.0, evaluator.evaluate("CEIL(2.9)", Map.of()));
        assertEquals(11.0, evaluator.evaluate("ceil(GP / 200000)", Map.of("GP", 2_000_001.0)));
    }

    @Test
    void evaluate_realPembulatanSeedFormula() {
        // Formula asli komponen PEMBULATAN (V16-V18): spasi ganda + CEIL nested.
        String formula = "( CEIL( PENGHASILAN_BERSIH / 100 ) * 100 ) - PENGHASILAN_BERSIH";
        assertEquals(33.0, evaluator.evaluate(formula, Map.of("PENGHASILAN_BERSIH", 1_234_567.0)));
    }

    @Test
    void evaluate_emptyOrNull_returnsZero() {
        assertEquals(0.0, evaluator.evaluate("", Map.of()));
        assertEquals(0.0, evaluator.evaluate("   ", Map.of()));
        assertEquals(0.0, evaluator.evaluate(null, Map.of()));
    }

    @Test
    void evaluate_invalidFormula_throwsGajiFormulaException() {
        GajiFormulaException ex = assertThrows(GajiFormulaException.class,
                () -> evaluator.evaluate("GP +", Map.of("GP", 1.0)));
        assertTrue(ex.getMessage().contains("GP +"),
                "Message must carry the formula, got: " + ex.getMessage());
    }

    @Test
    void evaluate_missingVariable_throwsGajiFormulaException() {
        GajiFormulaException ex = assertThrows(GajiFormulaException.class,
                () -> evaluator.evaluate("GP + LAIN", Map.of("GP", 1.0)));
        assertNotNull(ex.getCause());
    }
}
