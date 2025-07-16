package id.perumdamts.kepegawaian.helpers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DateHelper {
    /**
     * Format a LocalDate to a string using the "yyyy-MM-dd" format.
     *
     * @param date the LocalDate to format
     * @return the formatted string
     */
    public static String localDateToString(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * Counts the number of weekdays between the given start and end dates.
     * <p>
     * A weekday is defined as a day of the week that is not a Saturday or Sunday.
     *
     * @param startDate the start date of the period
     * @param endDate   the end date of the period
     * @return the number of weekdays between the start and end dates
     */
    public static int countWeekdaysBetween(LocalDate startDate, LocalDate endDate) {
        int weekdayCount = 0;

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            if (currentDate.getDayOfWeek().getValue() < 6) {
                weekdayCount++;
            }
            currentDate = currentDate.plusDays(1);
        }

        return weekdayCount;
    }

    /**
     * Creates a LocalDate object from the given year, month and day.
     *
     * @param year  the year of the date
     * @param month the month of the date, where 1 is January and 12 is December
     * @param day   the day of the month
     * @return the LocalDate object
     */
    public static LocalDate generateDate(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }

    /**
     * Returns a list of dates which are weekdays and not holidays.
     * <p>
     * A weekday is defined as a day of the week that is not a Saturday or Sunday.
     * <p>
     * The returned list will contain all dates from the given claimDates list that are
     * not in the holidayDates list and are weekdays.
     *
     * @param claimDates   the list of dates to filter
     * @param holidayDates the list of holidays to exclude
     * @return the list of working days
     */
    public static List<LocalDate> getWorkingDays(List<LocalDate> claimDates, List<LocalDate> holidayDates) {
        List<LocalDate> workingDays = new ArrayList<>();
        for (LocalDate date : claimDates) {
            if (!holidayDates.contains(date) && date.getDayOfWeek().getValue() < 6) {
                workingDays.add(date);
            }
        }
        return workingDays;
    }

    /**
     * Returns true if the leave request's start date is after the given month and day of the given year,
     * and the leave request's end date is before the given month and day of the given year.
     *
     * @param startDate  the start date of the leave request.
     * @param endDate    the end date of the leave request.
     * @param year       the year to check against.
     * @param startMonth the month to start checking from (inclusive).
     * @param endMonth   the month to end checking at (inclusive).
     * @param endDay     the day of the month to end checking at (inclusive).
     * @return true if the leave request's start date is after the given month and day of the given year,
     * and the leave request's end date is before the given month and day of the given year.
     */
    public static boolean isBetweenDates(LocalDate startDate, LocalDate endDate, int year, int startMonth, int endMonth, int endDay) {
        LocalDate start = DateHelper.generateDate(year, startMonth, 1).minusDays(1);
        LocalDate end = DateHelper.generateDate(year, endMonth, endDay).plusDays(1);

        return startDate.isAfter(start) && endDate.isBefore(end);
    }

    /**
     * Determines if the leave request overlaps between June 30th and July 1st of the given year.
     *
     * @param startDate the start date of the leave request.
     * @param endDate   the end date of the leave request.
     * @param year      the year to check against.
     * @return true if the leave request overlaps between June 30th and July 1st of the given year, false otherwise.
     */
    public static boolean isOverlappingDates(LocalDate startDate, LocalDate endDate, int year) {
        LocalDate june30th = DateHelper.generateDate(year, 6, 30);
        LocalDate july1st = DateHelper.generateDate(year, 7, 1);

        return startDate.isBefore(june30th.plusDays(1)) && endDate.isAfter(july1st.minusDays(1));
    }

}