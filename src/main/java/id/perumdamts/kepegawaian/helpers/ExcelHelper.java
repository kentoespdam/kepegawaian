package id.perumdamts.kepegawaian.helpers;

import org.apache.poi.ss.usermodel.*;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

public class ExcelHelper {
    public static void createCell(Row row, int colIndex, Object value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        cell.setCellStyle(style);
        switch (value) {
            case Integer i -> cell.setCellValue(i);
            case Double v -> cell.setCellValue(v);
            case Boolean b -> cell.setCellValue(b);
            case Long l -> cell.setCellValue(l);
            case Float v -> cell.setCellValue(v);
            case LocalDate localDate -> cell.setCellValue(DateHelper.localDateToString(localDate));
            case null, default -> cell.setCellValue((String) value);
        }
    }

    public static CellStyle createStyle(Row row, String[] styles) {
        String[] styleBorder = {"allBorder", "verticalBorder", "horizontalBorder"};
        String[] styleTextAlign = {"hCenter", "hLeft", "hRight", "vCenter"};
        String[] styleFont = {"bold", "italic"};

        Font font = row.getSheet().getWorkbook().createFont();
        font.setFontHeightInPoints((short) 9);
        CellStyle cellStyle = row.getSheet().getWorkbook().createCellStyle();
        for (String style : styles) {
            if (Arrays.asList(styleBorder).contains(style)) borderStyle(cellStyle, style);
            if (Arrays.asList(styleTextAlign).contains(style)) textAlign(cellStyle, style);
            if (Arrays.asList(styleFont).contains(style)) cellStyle.setFont(fontStyle(style, font));
        }
        return cellStyle;
    }

    static void textAlign(CellStyle cellStyle, String align) {
        switch (align) {
            case "hCenter":
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                break;
            case "hLeft":
                cellStyle.setAlignment(HorizontalAlignment.LEFT);
                break;
            case "hRight":
                cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                break;
            case "vCenter":
                cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                break;
        }
    }

    static Font fontStyle(String style, Font font) {
        switch (style) {
            case "bold":
                font.setBold(true);
                break;
            case "italic":
                font.setItalic(true);
                break;
        }
        return font;
    }

    static void borderStyle(CellStyle cellStyle, String style) {
        switch (style) {
            case "allBorder":
                cellStyle.setBorderBottom(BorderStyle.THIN);
                cellStyle.setBorderTop(BorderStyle.THIN);
                cellStyle.setBorderLeft(BorderStyle.THIN);
                cellStyle.setBorderRight(BorderStyle.THIN);
                break;
            case "verticalBorder":
                cellStyle.setBorderBottom(BorderStyle.THIN);
                cellStyle.setBorderTop(BorderStyle.THIN);
                break;
            case "horizontalBorder":
                cellStyle.setBorderLeft(BorderStyle.THIN);
                cellStyle.setBorderRight(BorderStyle.THIN);
                break;
        }
    }

    public static ByteArrayResource workbookToResource(Workbook workbook) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            workbook.write(byteArrayOutputStream);
            return new ByteArrayResource(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
