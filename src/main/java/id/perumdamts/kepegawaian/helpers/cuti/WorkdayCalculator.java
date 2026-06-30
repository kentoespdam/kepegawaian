package id.perumdamts.kepegawaian.helpers.cuti;

import java.time.LocalDate;
import java.util.Set;

public class WorkdayCalculator {
    public static int count(LocalDate tglMulai, LocalDate tglSelesai, Set<LocalDate> libur) {
        if (tglMulai == null || tglSelesai == null) {
            return 0;
        }
        int count = 0;
        LocalDate current = tglMulai;
        while (!current.isAfter(tglSelesai)) {
            int dayOfWeek = current.getDayOfWeek().getValue();
            boolean isWeekend = dayOfWeek >= 6; // 6 = Saturday, 7 = Sunday
            boolean isHoliday = libur != null && libur.contains(current);
            if (!isWeekend && !isHoliday) {
                count++;
            }
            current = current.plusDays(1);
        }
        return count;
    }
}
