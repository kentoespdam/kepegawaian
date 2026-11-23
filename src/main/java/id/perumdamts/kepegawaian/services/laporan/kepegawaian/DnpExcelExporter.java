package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.DnpResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

@Component
public class DnpExcelExporter {
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

  public ByteArrayResource export(List<DnpResponse> data) {
    var grouped = new LinkedHashMap<String, List<DnpResponse>>();
    for (var d : data) {
      String orgGroup = d.orgGroup();
      if (orgGroup == null || orgGroup.isBlank()) {
        orgGroup = "99. LAIN-LAIN";
      }
      grouped.computeIfAbsent(orgGroup, k -> new ArrayList<>()).add(d);
    }

    var sortedGroupKeys = new ArrayList<>(grouped.keySet());
    sortedGroupKeys.sort(
        (g1, g2) -> {
          int o1 = extractGroupOrder(g1);
          int o2 = extractGroupOrder(g2);
          if (o1 != o2) {
            return Integer.compare(o1, o2);
          }
          return g1.compareToIgnoreCase(g2);
        });

    try (var in = getClass().getResourceAsStream("/templates/laporan/template_dnp.xlsx")) {
      var wb = new XSSFWorkbook(in);
      var ws = wb.getSheetAt(0);

      var styles = createStyles(wb);
      setupHeader(ws, styles);

      int rowNum = 7;
      int rootUrut = 1;
      for (var groupKey : sortedGroupKeys) {
        var groupRow = ws.createRow(rowNum++);
        for (int c = 0; c < 16; c++) {
          var cell = groupRow.createCell(c);
          cell.setCellStyle(c == 0 ? styles.groupCol0Style() : styles.groupTextStyle());
        }
        groupRow.getCell(1).setCellValue(cleanGroupName(groupKey));
        ws.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 1, 15));

        var pegawaiList = grouped.get(groupKey);
        pegawaiList.sort(
            Comparator.comparingInt(p -> p.levelJabatan() != null ? p.levelJabatan() : 999));

        int childUrut = 1;
        for (var p : pegawaiList) {
          var row = ws.createRow(rowNum++);
          writeRow(row, p, rootUrut++, childUrut++, styles);
        }
      }

      var out = new ByteArrayOutputStream();
      wb.write(out);
      wb.close();
      return new ByteArrayResource(out.toByteArray());
    } catch (IOException e) {
      throw new IllegalStateException("Failed to generate DNP Excel", e);
    }
  }

  void setupHeader(Sheet ws, DnpStyles s) {
    for (int i = ws.getNumMergedRegions() - 1; i >= 0; i--) {
      var region = ws.getMergedRegion(i);
      if (region.getFirstRow() <= 6) {
        ws.removeMergedRegion(i);
      }
    }

    // Row 0 (Title 1)
    var r0 = ws.getRow(0);
    if (r0 == null) r0 = ws.createRow(0);
    for (int c = 0; c < 16; c++) {
      var cell = r0.getCell(c);
      if (cell == null) cell = r0.createCell(c);
    }
    r0.getCell(0).setCellValue("DAFTAR NOMINATIF PEGAWAI");
    r0.getCell(0).setCellStyle(s.titleStyle());
    ws.addMergedRegion(new CellRangeAddress(0, 0, 0, 15));

    // Row 1 (Title 2)
    var now = LocalDate.now();
    String bulan = now.getMonth().getDisplayName(TextStyle.FULL, Locale.of("id", "ID")).toUpperCase();
    String bulanTitle = "BULAN: " + bulan + " " + now.getYear();
    var r1 = ws.getRow(1);
    if (r1 == null) r1 = ws.createRow(1);
    for (int c = 0; c < 16; c++) {
      var cell = r1.getCell(c);
      if (cell == null) cell = r1.createCell(c);
    }
    r1.getCell(0).setCellValue(bulanTitle);
    r1.getCell(0).setCellStyle(s.titleStyle());
    ws.addMergedRegion(new CellRangeAddress(1, 1, 0, 15));

    // Rows 3, 4, 5, 6: Table Header
    for (int r = 3; r <= 6; r++) {
      var row = ws.getRow(r);
      if (row == null) row = ws.createRow(r);
      for (int c = 0; c < 16; c++) {
        var cell = row.getCell(c);
        if (cell == null) cell = row.createCell(c);
        cell.setCellStyle(s.headerStyle());
      }
    }

    // Row 3 (Excel Row 4) values
    ws.getRow(3).getCell(0).setCellValue("NO");
    ws.getRow(3).getCell(2).setCellValue("NAMA");
    ws.getRow(3).getCell(3).setCellValue("NIPAM");
    ws.getRow(3).getCell(4).setCellValue("JABATAN");
    ws.getRow(3).getCell(5).setCellValue("TMT JABATAN");
    ws.getRow(3).getCell(6).setCellValue("PANGKAT");
    ws.getRow(3).getCell(7).setCellValue("GOL.");
    ws.getRow(3).getCell(8).setCellValue("TMT");
    ws.getRow(3).getCell(9).setCellValue("MASA KERJA GOL. TERAKHIR");
    ws.getRow(3).getCell(11).setCellValue("MASA KERJA");
    ws.getRow(3).getCell(14).setCellValue("PENDIDIKAN");
    ws.getRow(3).getCell(15).setCellValue("TEMPAT/TGL. LAHIR");

    // Row 4 (Excel Row 5) values
    ws.getRow(4).getCell(11).setCellValue("MULAI BEKERJA");
    ws.getRow(4).getCell(12).setCellValue("SELURUH");

    // Row 5 (Excel Row 6) values
    ws.getRow(5).getCell(9).setCellValue("TH");
    ws.getRow(5).getCell(10).setCellValue("BLN");
    ws.getRow(5).getCell(12).setCellValue("TH");
    ws.getRow(5).getCell(13).setCellValue("BLN");

    // Row 6 (Excel Row 7) Column Numbering values
    ws.getRow(6).getCell(0).setCellValue("");
    ws.getRow(6).getCell(1).setCellValue("1");
    ws.getRow(6).getCell(2).setCellValue("2");
    ws.getRow(6).getCell(3).setCellValue("3");
    ws.getRow(6).getCell(4).setCellValue("4");
    ws.getRow(6).getCell(5).setCellValue("5");
    ws.getRow(6).getCell(6).setCellValue("6");
    ws.getRow(6).getCell(7).setCellValue("7");
    ws.getRow(6).getCell(8).setCellValue("8");
    ws.getRow(6).getCell(9).setCellValue("9");
    ws.getRow(6).getCell(11).setCellValue("10");
    ws.getRow(6).getCell(12).setCellValue("11");
    ws.getRow(6).getCell(14).setCellValue("12");
    ws.getRow(6).getCell(15).setCellValue("13");

    // Header Merged Regions
    ws.addMergedRegion(new CellRangeAddress(3, 5, 0, 1));
    ws.addMergedRegion(new CellRangeAddress(3, 5, 2, 2));
    ws.addMergedRegion(new CellRangeAddress(3, 5, 3, 3));
    ws.addMergedRegion(new CellRangeAddress(3, 5, 4, 4));
    ws.addMergedRegion(new CellRangeAddress(3, 5, 5, 5));
    ws.addMergedRegion(new CellRangeAddress(3, 5, 6, 6));
    ws.addMergedRegion(new CellRangeAddress(3, 5, 7, 7));
    ws.addMergedRegion(new CellRangeAddress(3, 5, 8, 8));
    ws.addMergedRegion(new CellRangeAddress(3, 4, 9, 10));
    ws.addMergedRegion(new CellRangeAddress(3, 3, 11, 13));
    ws.addMergedRegion(new CellRangeAddress(4, 5, 11, 11));
    ws.addMergedRegion(new CellRangeAddress(4, 4, 12, 13));
    ws.addMergedRegion(new CellRangeAddress(3, 5, 14, 14));
    ws.addMergedRegion(new CellRangeAddress(3, 5, 15, 15));

    // Baris Nomor Kolom Merged Regions
    ws.addMergedRegion(new CellRangeAddress(6, 6, 9, 10));
    ws.addMergedRegion(new CellRangeAddress(6, 6, 12, 13));
  }

  private void writeRow(Row row, DnpResponse p, int rootUrut, int childUrut, DnpStyles s) {
    var c0 = row.createCell(0);
    c0.setCellValue(rootUrut);
    c0.setCellStyle(s.dataCenterStyle());

    var c1 = row.createCell(1);
    c1.setCellValue(childUrut);
    c1.setCellStyle(s.dataCenterStyle());

    var c2 = row.createCell(2);
    c2.setCellValue(p.nama() != null ? p.nama() : "-");
    c2.setCellStyle(s.dataLeftStyle());

    var c3 = row.createCell(3);
    c3.setCellValue(p.nipam() != null ? p.nipam() : "-");
    c3.setCellStyle(s.dataCenterStyle());

    var c4 = row.createCell(4);
    c4.setCellValue(p.namaJabatan() != null ? p.namaJabatan() : "-");
    c4.setCellStyle(s.dataLeftStyle());

    var c5 = row.createCell(5);
    c5.setCellValue(formatDate(p.tmtJabatan()));
    c5.setCellStyle(s.dataCenterStyle());

    var c6 = row.createCell(6);
    c6.setCellValue(p.pangkat() != null ? p.pangkat() : "-");
    c6.setCellStyle(s.dataLeftStyle());

    var c7 = row.createCell(7);
    c7.setCellValue(p.golongan() != null ? p.golongan() : "-");
    c7.setCellStyle(s.dataCenterStyle());

    var c8 = row.createCell(8);
    c8.setCellValue(formatDate(p.tmtGolongan()));
    c8.setCellStyle(s.dataCenterStyle());

    writeNumericOrDash(row, 9, p.mkgTahun(), s);
    writeNumericOrDash(row, 10, p.mkgBulan(), s);

    var c11 = row.createCell(11);
    c11.setCellValue(formatDate(p.tmtKerja()));
    c11.setCellStyle(s.dataCenterStyle());

    writeNumericOrDash(row, 12, p.mkTahun(), s);
    writeNumericOrDash(row, 13, p.mkBulan(), s);

    var c14 = row.createCell(14);
    c14.setCellValue(p.pendidikan() != null ? p.pendidikan() : "-");
    c14.setCellStyle(s.dataLeftStyle());

    var c15 = row.createCell(15);
    c15.setCellValue(p.ttl() != null ? p.ttl() : "-");
    c15.setCellStyle(s.dataLeftStyle());
  }

  private void writeNumericOrDash(Row row, int col, Integer val, DnpStyles s) {
    var cell = row.createCell(col);
    if (val == null) {
      cell.setCellValue("-");
      cell.setCellStyle(s.dataCenterStyle());
    } else {
      cell.setCellValue(val);
      cell.setCellStyle(s.dataCenterNumericStyle());
    }
  }

  private static String formatDate(LocalDate date) {
    return date != null ? date.format(DATE_FORMATTER) : "-";
  }

  static int extractGroupOrder(String orgGroup) {
    if (orgGroup == null || orgGroup.isBlank()) {
      return 999;
    }
    var m = Pattern.compile("^\\s*(\\d+)").matcher(orgGroup);
    if (m.find()) {
      try {
        return Integer.parseInt(m.group(1));
      } catch (NumberFormatException ignored) {
      }
    }
    return 999;
  }

  static String cleanGroupName(String orgGroup) {
    if (orgGroup == null || orgGroup.isBlank()) {
      return "";
    }
    String cleaned = orgGroup.replaceFirst("^\\s*\\d+\\s*[.\\-_\\s]*", "").trim();
    if (cleaned.isEmpty()) {
      cleaned = orgGroup.trim();
    }
    return cleaned.toUpperCase(Locale.ROOT);
  }

  record DnpStyles(
      CellStyle titleStyle,
      CellStyle headerStyle,
      CellStyle groupCol0Style,
      CellStyle groupTextStyle,
      CellStyle dataCenterStyle,
      CellStyle dataCenterNumericStyle,
      CellStyle dataLeftStyle) {}

  DnpStyles createStyles(Workbook wb) {
    var fontBold12 = wb.createFont();
    fontBold12.setBold(true);
    fontBold12.setFontHeightInPoints((short) 12);

    var fontBold11 = wb.createFont();
    fontBold11.setBold(true);
    fontBold11.setFontHeightInPoints((short) 11);

    var fontNormal11 = wb.createFont();
    fontNormal11.setFontHeightInPoints((short) 11);

    var titleStyle = wb.createCellStyle();
    titleStyle.setFont(fontBold12);
    titleStyle.setAlignment(HorizontalAlignment.CENTER);
    titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

    var headerStyle = wb.createCellStyle();
    headerStyle.setFont(fontBold11);
    headerStyle.setAlignment(HorizontalAlignment.CENTER);
    headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    headerStyle.setWrapText(true);
    applyThinBorders(headerStyle);

    var groupCol0Style = wb.createCellStyle();
    groupCol0Style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    groupCol0Style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    applyThinBorders(groupCol0Style);

    var groupTextStyle = wb.createCellStyle();
    groupTextStyle.setFont(fontBold11);
    groupTextStyle.setAlignment(HorizontalAlignment.LEFT);
    groupTextStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    groupTextStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    groupTextStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    applyThinBorders(groupTextStyle);

    var dataCenterStyle = wb.createCellStyle();
    dataCenterStyle.setFont(fontNormal11);
    dataCenterStyle.setAlignment(HorizontalAlignment.CENTER);
    dataCenterStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    applyThinBorders(dataCenterStyle);

    var dataCenterNumericStyle = wb.createCellStyle();
    dataCenterNumericStyle.setFont(fontNormal11);
    dataCenterNumericStyle.setAlignment(HorizontalAlignment.CENTER);
    dataCenterNumericStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    dataCenterNumericStyle.setDataFormat(wb.createDataFormat().getFormat("0"));
    applyThinBorders(dataCenterNumericStyle);

    var dataLeftStyle = wb.createCellStyle();
    dataLeftStyle.setFont(fontNormal11);
    dataLeftStyle.setAlignment(HorizontalAlignment.LEFT);
    dataLeftStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    applyThinBorders(dataLeftStyle);

    return new DnpStyles(
        titleStyle,
        headerStyle,
        groupCol0Style,
        groupTextStyle,
        dataCenterStyle,
        dataCenterNumericStyle,
        dataLeftStyle);
  }

  private static void applyThinBorders(CellStyle style) {
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
  }
}
