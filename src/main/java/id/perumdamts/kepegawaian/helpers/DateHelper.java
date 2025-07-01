package id.perumdamts.kepegawaian.helpers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateHelper {
    public static String localDateToString(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDate.format(formatter);
    }

    public static int countWeekdaysBetween(LocalDate start, LocalDate end) {
        int weekdayCount = 0;
        while (start.isBefore(end.plusDays(1))) {
            if (start.getDayOfWeek().getValue() < 6) {
                weekdayCount++;
            }
            start = start.plusDays(1);
        }
        return weekdayCount;
    }

    public static LocalDate generateDate(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }
}