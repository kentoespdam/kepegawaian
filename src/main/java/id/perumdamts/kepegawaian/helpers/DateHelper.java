package id.perumdamts.kepegawaian.helpers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateHelper {
    public static String localDateToString(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDate.format(formatter);
    }
}
