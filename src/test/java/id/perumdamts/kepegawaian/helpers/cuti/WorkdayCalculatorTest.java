package id.perumdamts.kepegawaian.helpers.cuti;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorkdayCalculatorTest {

    @Test
    void testCountNormalWeekdays() {
        // Monday (June 23, 2025) to Friday (June 27, 2025) -> 5 days
        LocalDate start = LocalDate.of(2025, 6, 23);
        LocalDate end = LocalDate.of(2025, 6, 27);
        assertEquals(5, WorkdayCalculator.count(start, end, Set.of()));
    }

    @Test
    void testCountWithWeekendOnly() {
        // Saturday (June 28, 2025) to Sunday (June 29, 2025) -> 0 days
        LocalDate start = LocalDate.of(2025, 6, 28);
        LocalDate end = LocalDate.of(2025, 6, 29);
        assertEquals(0, WorkdayCalculator.count(start, end, Set.of()));
    }

    @Test
    void testCountWithWeekdayHoliday() {
        // Monday (June 23, 2025) to Friday (June 27, 2025) with Wednesday as holiday -> 4 days
        LocalDate start = LocalDate.of(2025, 6, 23);
        LocalDate end = LocalDate.of(2025, 6, 27);
        Set<LocalDate> holidays = Set.of(LocalDate.of(2025, 6, 25));
        assertEquals(4, WorkdayCalculator.count(start, end, holidays));
    }

    @Test
    void testCountWithWeekendHolidayNoDoubleSubtract() {
        // Friday (June 27, 2025) to Monday (June 30, 2025) -> contains Sat, Sun.
        // If Saturday is a holiday, it shouldn't be subtracted double.
        // Fri (1), Sat (0), Sun (0), Mon (1) -> 2 days.
        LocalDate start = LocalDate.of(2025, 6, 27);
        LocalDate end = LocalDate.of(2025, 6, 30);
        Set<LocalDate> holidays = Set.of(LocalDate.of(2025, 6, 28)); // Sat is holiday
        assertEquals(2, WorkdayCalculator.count(start, end, holidays));
    }

    @Test
    void testCountCrossMonth() {
        // June 27, 2025 (Friday) to July 2, 2025 (Wednesday)
        // Fri (1), Sat (0), Sun (0), Mon (1), Tue (1), Wed (1) -> 4 days.
        LocalDate start = LocalDate.of(2025, 6, 27);
        LocalDate end = LocalDate.of(2025, 7, 2);
        assertEquals(4, WorkdayCalculator.count(start, end, Set.of()));
    }
}
