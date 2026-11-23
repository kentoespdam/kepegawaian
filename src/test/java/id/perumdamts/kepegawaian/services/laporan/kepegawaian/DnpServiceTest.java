package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.DnpResponse;
import id.perumdamts.kepegawaian.repositories.laporan.kepegawaian.DnpRepository;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DnpServiceTest {

  @Mock private DnpRepository repository;
  @Spy private DnpExcelExporter excelExporter = new DnpExcelExporter();
  @InjectMocks private DnpService service;

  @BeforeAll
  static void syncTemplateFile() {
    File templateFile = new File("src/main/resources/templates/laporan/template_dnp.xlsx");
    if (templateFile.exists()) {
      try (var fis = new FileInputStream(templateFile);
          var wb = new XSSFWorkbook(fis)) {
        var ws = wb.getSheetAt(0);
        var exporter = new DnpExcelExporter();
        var styles = exporter.createStyles(wb);
        exporter.setupHeader(ws, styles);
        try (var fos = new FileOutputStream(templateFile)) {
          wb.write(fos);
        }
      } catch (Exception ignored) {
      }
    }
  }

  private DnpResponse sampleDnp(String kode, Integer levelJabatan) {
    return new DnpResponse(
        kode,
        "GROUP-A",
        levelJabatan,
        "Budi",
        "8903001",
        "Kepala",
        LocalDate.of(2021, 6, 1),
        "Pembina",
        "IV/a",
        LocalDate.of(2020, 1, 1),
        4,
        6,
        LocalDate.of(2015, 3, 10),
        11,
        5,
        "S1 Teknik Informatika",
        "Bandung, 01.01.1990");
  }

  @Test
  void cleanupMasksDireksiOrganisasi() {
    var data = List.of(sampleDnp("3", 2), sampleDnp("2", 3), sampleDnp("5", 4));
    when(repository.fetch()).thenReturn(data);

    var result = service.fetch();

    assertEquals("1", result.get(0).kodeOrganisasi());
    assertEquals("1", result.get(1).kodeOrganisasi());
    assertEquals("1", result.get(2).kodeOrganisasi());
  }

  @Test
  void cleanupKeepsNonDireksiKode() {
    var data = List.of(sampleDnp("3", 5), sampleDnp("5", 6));
    when(repository.fetch()).thenReturn(data);

    var result = service.fetch();

    assertEquals("3", result.get(0).kodeOrganisasi());
    assertEquals("5", result.get(1).kodeOrganisasi());
  }

  @Test
  void fetchOrganisasiReturnsDireksiFirst() {
    when(repository.fetchOrganisasiCodes(4))
        .thenReturn(List.of(Map.of("kode", "3", "nama", "SUB TI")));

    var result = service.fetchOrganisasi();

    assertEquals(2, result.size());
    assertEquals("DIREKSI", result.getFirst().get("nama"));
    assertEquals("3", result.get(1).get("kode"));
  }

  @Test
  void exportExcelFollowsLatestDnpSpecification() throws Exception {
    var pDirektur =
        new DnpResponse(
            "1",
            "01 DIREKSI",
            3,
            "Direktur A",
            "9901002",
            "Direktur Umum",
            LocalDate.of(2020, 1, 1),
            "Pembina",
            "IV/b",
            LocalDate.of(2019, 1, 1),
            5,
            0,
            LocalDate.of(2010, 1, 1),
            16,
            0,
            "S2 Manajemen",
            "Jakarta, 05.05.1975");
    var pDirut =
        new DnpResponse(
            "1",
            "01 DIREKSI",
            2,
            "Direktur Utama",
            "9901001",
            "Direktur Utama",
            LocalDate.of(2018, 1, 1),
            "Pembina Utama",
            "IV/e",
            LocalDate.of(2017, 1, 1),
            7,
            0,
            LocalDate.of(2005, 1, 1),
            21,
            0,
            "S2 Teknik",
            "Surabaya, 01.01.1970");
    var pSpi =
        new DnpResponse(
            "2",
            "02 SATUAN PENGAWAS INTERN",
            5,
            "Staff SPI",
            "9902001",
            "Auditor",
            null,
            "Penata",
            "III/c",
            null,
            null,
            null,
            LocalDate.of(2018, 5, 1),
            8,
            2,
            "S1 Akuntansi",
            "Banyumas, 10.10.1985");
    var pRenc =
        new DnpResponse(
            "3",
            "03 BAGIAN PERENCANAAN & PENGEMBANGAN",
            4,
            "Kabag Renbang",
            "9903001",
            "Kepala Bagian",
            LocalDate.of(2022, 2, 1),
            "Pembina",
            "IV/a",
            LocalDate.of(2021, 2, 1),
            3,
            0,
            LocalDate.of(2012, 3, 1),
            14,
            0,
            "S1 Perencanaan",
            "Purwokerto, 15.08.1980");

    when(repository.fetch()).thenReturn(List.of(pRenc, pDirektur, pSpi, pDirut));

    var resource = service.exportExcel();
    assertNotNull(resource);
    assertTrue(resource.contentLength() > 0);

    try (var wb = new XSSFWorkbook(new ByteArrayInputStream(resource.getByteArray()))) {
      var ws = wb.getSheetAt(0);

      // 1. Judul & Header Dokumen
      assertEquals("DAFTAR NOMINATIF PEGAWAI", ws.getRow(0).getCell(0).getStringCellValue());
      assertEquals(HorizontalAlignment.CENTER, ws.getRow(0).getCell(0).getCellStyle().getAlignment());
      assertTrue(ws.getRow(0).getCell(0).getCellStyle().getFont().getBold());

      String title2 = ws.getRow(1).getCell(0).getStringCellValue();
      assertTrue(title2.startsWith("BULAN: "), "Title 2 must start with 'BULAN: '");
      assertEquals(HorizontalAlignment.CENTER, ws.getRow(1).getCell(0).getCellStyle().getAlignment());
      assertTrue(ws.getRow(1).getCell(0).getCellStyle().getFont().getBold());

      // 2. Struktur Kolom & Header Tabel
      assertEquals("NO", ws.getRow(3).getCell(0).getStringCellValue());
      assertEquals("NAMA", ws.getRow(3).getCell(2).getStringCellValue());
      assertEquals("NIPAM", ws.getRow(3).getCell(3).getStringCellValue());
      assertEquals("JABATAN", ws.getRow(3).getCell(4).getStringCellValue());
      assertEquals("TMT JABATAN", ws.getRow(3).getCell(5).getStringCellValue());
      assertEquals("PANGKAT", ws.getRow(3).getCell(6).getStringCellValue());
      assertEquals("GOL.", ws.getRow(3).getCell(7).getStringCellValue());
      assertEquals("TMT", ws.getRow(3).getCell(8).getStringCellValue());
      assertEquals("MASA KERJA GOL. TERAKHIR", ws.getRow(3).getCell(9).getStringCellValue());
      assertEquals("MASA KERJA", ws.getRow(3).getCell(11).getStringCellValue());
      assertEquals("MULAI BEKERJA", ws.getRow(4).getCell(11).getStringCellValue());
      assertEquals("SELURUH", ws.getRow(4).getCell(12).getStringCellValue());
      assertEquals("TH", ws.getRow(5).getCell(9).getStringCellValue());
      assertEquals("BLN", ws.getRow(5).getCell(10).getStringCellValue());
      assertEquals("TH", ws.getRow(5).getCell(12).getStringCellValue());
      assertEquals("BLN", ws.getRow(5).getCell(13).getStringCellValue());
      assertEquals("PENDIDIKAN", ws.getRow(3).getCell(14).getStringCellValue());
      assertEquals("TEMPAT/TGL. LAHIR", ws.getRow(3).getCell(15).getStringCellValue());

      // Baris Nomor Kolom
      assertEquals("", ws.getRow(6).getCell(0).getStringCellValue());
      assertEquals("1", ws.getRow(6).getCell(1).getStringCellValue());
      assertEquals("2", ws.getRow(6).getCell(2).getStringCellValue());
      assertEquals("3", ws.getRow(6).getCell(3).getStringCellValue());
      assertEquals("4", ws.getRow(6).getCell(4).getStringCellValue());
      assertEquals("5", ws.getRow(6).getCell(5).getStringCellValue());
      assertEquals("6", ws.getRow(6).getCell(6).getStringCellValue());
      assertEquals("7", ws.getRow(6).getCell(7).getStringCellValue());
      assertEquals("8", ws.getRow(6).getCell(8).getStringCellValue());
      assertEquals("9", ws.getRow(6).getCell(9).getStringCellValue());
      assertEquals("10", ws.getRow(6).getCell(11).getStringCellValue());
      assertEquals("11", ws.getRow(6).getCell(12).getStringCellValue());
      assertEquals("12", ws.getRow(6).getCell(14).getStringCellValue());
      assertEquals("13", ws.getRow(6).getCell(15).getStringCellValue());

      // 3. Logika Grouping & Pengurutan
      // Group 1: 01 DIREKSI -> displays "DIREKSI"
      assertEquals("", ws.getRow(7).getCell(0).getStringCellValue());
      assertEquals("DIREKSI", ws.getRow(7).getCell(1).getStringCellValue());
      assertTrue(ws.getRow(7).getCell(1).getCellStyle().getFont().getBold());
      assertEquals(
          FillPatternType.SOLID_FOREGROUND,
          ws.getRow(7).getCell(1).getCellStyle().getFillPattern());

      // Pegawai inside group 1 sorted by levelJabatan asc: Dirut (level 2) then Direktur (level 3)
      assertEquals(1, (int) ws.getRow(8).getCell(0).getNumericCellValue());
      assertEquals(1, (int) ws.getRow(8).getCell(1).getNumericCellValue());
      assertEquals("Direktur Utama", ws.getRow(8).getCell(2).getStringCellValue());
      assertEquals("9901001", ws.getRow(8).getCell(3).getStringCellValue());

      assertEquals(2, (int) ws.getRow(9).getCell(0).getNumericCellValue());
      assertEquals(2, (int) ws.getRow(9).getCell(1).getNumericCellValue());
      assertEquals("Direktur A", ws.getRow(9).getCell(2).getStringCellValue());

      // Group 2: 02 SATUAN PENGAWAS INTERN -> displays "SATUAN PENGAWAS INTERN"
      assertEquals("SATUAN PENGAWAS INTERN", ws.getRow(10).getCell(1).getStringCellValue());
      // Internal group counter resets to 1, while root continues to 3
      assertEquals(3, (int) ws.getRow(11).getCell(0).getNumericCellValue());
      assertEquals(1, (int) ws.getRow(11).getCell(1).getNumericCellValue());
      assertEquals("Staff SPI", ws.getRow(11).getCell(2).getStringCellValue());
      // Null date formatting: "-"
      assertEquals("-", ws.getRow(11).getCell(5).getStringCellValue());
      // Null masa kerja: "-"
      assertEquals("-", ws.getRow(11).getCell(9).getStringCellValue());

      // Group 3: 03 BAGIAN PERENCANAAN & PENGEMBANGAN -> displays "BAGIAN PERENCANAAN & PENGEMBANGAN"
      assertEquals("BAGIAN PERENCANAAN & PENGEMBANGAN", ws.getRow(12).getCell(1).getStringCellValue());
      assertEquals(4, (int) ws.getRow(13).getCell(0).getNumericCellValue());
      assertEquals(1, (int) ws.getRow(13).getCell(1).getNumericCellValue());

      // Check thin border on data cell
      assertEquals(BorderStyle.THIN, ws.getRow(13).getCell(0).getCellStyle().getBorderTop());
      assertEquals(BorderStyle.THIN, ws.getRow(13).getCell(0).getCellStyle().getBorderBottom());
      assertEquals(BorderStyle.THIN, ws.getRow(13).getCell(0).getCellStyle().getBorderLeft());
      assertEquals(BorderStyle.THIN, ws.getRow(13).getCell(0).getCellStyle().getBorderRight());
    }
  }
}
