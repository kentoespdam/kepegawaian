package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EFilterKontrak;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.IdentityHashMap;
import java.util.Locale;

/**
 * Matches ALL Python excel_helper + route title formats exactly.
 */
class ExcelDateHelper {
    private static final Locale ID = Locale.of("id", "ID");

    /** "dd MonthName yyyy" — matches Python format_date_vectorized */
    static String formatDate(LocalDate date) {
        if (date == null) return null;
        return date.getDayOfMonth() + " "
                + date.getMonth().getDisplayName(TextStyle.FULL, ID) + " "
                + date.getYear();
    }

    static String monthName(int month, int year) {
        return LocalDate.of(year, month, 1).getMonth()
                .getDisplayName(TextStyle.FULL, ID) + " " + year;
    }

    /** DUK/DNP: "BULAN : Agustus 2026" */
    static String bulanTitle() {
        var now = LocalDate.now();
        return "BULAN : " + monthName(now.getMonthValue(), now.getYear());
    }

    /** Mutasi: "Periode: 01-Januari-2024 s/d 31-Desember-2024" */
    static String mutasiTitle(LocalDate from, LocalDate to) {
        return "Periode: " + formatDate(from).replace(" ", "-")
                + " s/d " + formatDate(to).replace(" ", "-");
    }

    /** Kontrak — filter-dependent title matching Python route */
    static String kontrakTitle(EFilterKontrak filter) {
        var now = LocalDate.now();
        return switch (filter) {
            case AKTIF -> "Bulan " + monthName(now.getMonthValue(), now.getYear());
            case THIS_MONTH -> "Berakhir Bulan " + monthName(now.getMonthValue(), now.getYear());
            case GTE_1_MONTH -> "Berakhir Bulan " + monthName(now.plusMonths(1).getMonthValue(), now.plusMonths(1).getYear());
            case GTE_2_MONTH -> "Berakhir Bulan " + monthName(now.plusMonths(2).getMonthValue(), now.plusMonths(2).getYear());
            case GTE_3_MONTH -> "Berakhir Bulan " + monthName(now.plusMonths(3).getMonthValue(), now.plusMonths(3).getYear());
            case ENDED -> "Telah Berakhir Bulan " + monthName(now.plusMonths(4).getMonthValue(), now.plusMonths(4).getYear());
        };
    }

    /** LTA/KB: "Bulan: Agustus 2026" */
    static String bulanColonTitle() {
        var now = LocalDate.now();
        return "Bulan: " + monthName(now.getMonthValue(), now.getYear());
    }

    /** Pendidikan2: "BULAN : Agustus 2026" */
    static String bulanTitle(int tahun, int bulan) {
        return "BULAN : " + monthName(bulan, tahun);
    }

    // ── Per-workbook CellStyle cache (avoids cross-workbook CellStyle sharing) ──

    private static final IdentityHashMap<Workbook, CellStyle> boldCache = new IdentityHashMap<>();
    private static final IdentityHashMap<Workbook, CellStyle> borderCache = new IdentityHashMap<>();

    static CellStyle boldStyle(Workbook wb) {
        return boldCache.computeIfAbsent(wb, w -> {
            var style = w.createCellStyle();
            var font = w.createFont();
            font.setBold(true);
            style.setFont(font);
            return style;
        });
    }

    static CellStyle borderStyle(Workbook wb) {
        return borderCache.computeIfAbsent(wb, w -> {
            var style = w.createCellStyle();
            var thin = BorderStyle.THIN;
            style.setBorderTop(thin);
            style.setBorderBottom(thin);
            style.setBorderLeft(thin);
            style.setBorderRight(thin);
            return style;
        });
    }

    /** Write a cell with value + optional styles ("bold", "allborder") */
    static void writeStyledCell(Row row, int col, Object value, String... styles) {
        var cell = row.createCell(col);
        if (value == null) { cell.setBlank(); return; }
        switch (value) {
            case Number n -> cell.setCellValue(n.doubleValue());
            default -> cell.setCellValue(String.valueOf(value));
        }
        var wb = row.getSheet().getWorkbook();
        for (var style : styles) {
            switch (style) {
                case "bold" -> cell.setCellStyle(boldStyle(wb));
                case "allborder" -> cell.setCellStyle(borderStyle(wb));
            }
        }
    }

    /** Set title cell + merge. Creates row if missing. Skips merge if already exists. */
    static void setTitle(Row row, Sheet sheet, String title, int mergeEndCol) {
        if (row == null) row = sheet.createRow(1);
        var cell = row.getCell(0);
        if (cell == null) cell = row.createCell(0);
        cell.setCellValue(title);
        cell.setCellStyle(boldStyle(sheet.getWorkbook()));
        int r = row.getRowNum();
        boolean alreadyMerged = false;
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            var region = sheet.getMergedRegion(i);
            if (region.getFirstRow() == r && region.getLastRow() == r) {
                alreadyMerged = true;
                break;
            }
        }
        if (!alreadyMerged) {
            sheet.addMergedRegion(new CellRangeAddress(r, r, 0, mergeEndCol));
        }
    }
}
