package id.perumdamts.kepegawaian.helpers.cuti;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MinimalCutiRuleTest {

    @Test
    void testCheckSufficientDays() {
        // Taking 3 days when quota is 5 -> should pass
        assertDoesNotThrow(() -> MinimalCutiRule.check(3, 5));
    }

    @Test
    void testCheckLessDaysWithHighQuota() {
        // Taking 2 days when quota is 5 (>= 3) -> should throw "minimal 3 hari"
        assertThrows(RuntimeException.class, () -> MinimalCutiRule.check(2, 5));
    }

    @Test
    void testCheckLessDaysWithLowQuotaPartial() {
        // Taking 1 day when quota is 2 (which is < 3) -> should throw "Sisa Kuota Cuti ... harus diambil semua"
        assertThrows(RuntimeException.class, () -> MinimalCutiRule.check(1, 2));
    }

    @Test
    void testCheckLessDaysWithLowQuotaAll() {
        // Taking 2 days when quota is 2 -> should pass
        assertDoesNotThrow(() -> MinimalCutiRule.check(2, 2));
    }
}
