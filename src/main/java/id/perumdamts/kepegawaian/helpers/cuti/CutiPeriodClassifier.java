package id.perumdamts.kepegawaian.helpers.cuti;

import id.perumdamts.kepegawaian.entities.commons.ECutiPeriod;
import id.perumdamts.kepegawaian.helpers.DateHelper;

import java.time.LocalDate;

public final class CutiPeriodClassifier {
    private CutiPeriodClassifier() {}

    public static ECutiPeriod classify(LocalDate start, LocalDate end, int nowYear) {
        int startYear = start.getYear();
        int endYear = end.getYear();

        if (startYear > nowYear && endYear > nowYear) {
            return ECutiPeriod.NEXT_YEAR;
        }
        if (startYear == nowYear && endYear > startYear) {
            return ECutiPeriod.OVERLAPPING;
        }
        if (DateHelper.isBetweenDates(start, end, startYear, 1, 6, 30)) {
            return ECutiPeriod.JAN_JUN;
        }
        if (DateHelper.isBetweenDates(start, end, startYear, 7, 12, 31)) {
            return ECutiPeriod.JUL_DES;
        }
        if (DateHelper.isOverlappingDates(start, end, startYear)) {
            return ECutiPeriod.JUN_JUL;
        }
        throw new IllegalArgumentException("Invalid date range for cuti period classification");
    }
}
