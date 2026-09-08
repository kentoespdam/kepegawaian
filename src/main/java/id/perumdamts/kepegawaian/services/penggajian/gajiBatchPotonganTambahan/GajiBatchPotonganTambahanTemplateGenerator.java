package id.perumdamts.kepegawaian.services.penggajian.gajiBatchPotonganTambahan;

import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class GajiBatchPotonganTambahanTemplateGenerator {
    private final GajiBatchMasterRepository masterRepository;

    // label kategori standar form Potongan Gaji (baris 10, kolom E..X)
    private static final List<String> KATEGORI = List.of(
            "PINJ.KOPR.", "SIMPANAN KOPERASI", "DH.WANITA", "PINJ.DH.WNT", "YAKAN",
            "KORPRI", "BRI", "BPD", "BTN", "Gn. SIMPING",
            "BANK.GG.SLAMET", "BKK UTARA", "BKK SELATAN", "DPLK", "LISTRIK",
            "AIR", "ZAKAT", "JKN", "PMI", "LAIN-LAIN");

    private static final int HEADER_ROW = 8;   // baris 9: NO. | NAMA | NIPAM | GAJI | POTONGAN | JUMLAH POTONGAN | GAJI BERSIH
    private static final int LABEL_ROW = 9;    // baris 10: label kategori mulai kolom E
    private static final int FIRST_DATA_ROW = 10; // baris 11 dst
    private static final int LABEL_START_COLUMN = 4; // kolom E

    public byte[] generateForRootBatchId(String rootBatchId) throws IOException {
        List<GajiBatchMaster> masters = masterRepository.findByGajiBatchRoot_Id(rootBatchId);
        if (masters.isEmpty()) {
            throw new NotFoundException("Tidak ada GajiBatchMaster untuk root batch ini");
        }
        String periode = masters.getFirst().getPeriode();

        Map<String, List<GajiBatchMaster>> byOrg = masters.stream()
                .collect(Collectors.groupingBy(m -> m.getNamaOrganisasi() != null ? m.getNamaOrganisasi() : "ORGANISASI"));

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            for (var entry : byOrg.entrySet()) {
                Sheet sheet = workbook.createSheet(trimSheetName(entry.getKey()));
                // judul A6 + periode A7
                sheet.createRow(5).createCell(0).setCellValue("DAFTAR POTONGAN GAJI PEGAWAI");
                sheet.createRow(6).createCell(0).setCellValue("BULAN " + periode);

                // header tabel baris 9
                Row header = sheet.createRow(HEADER_ROW);
                header.createCell(0).setCellValue("NO.");
                header.createCell(1).setCellValue("NAMA");
                header.createCell(2).setCellValue("NIPAM");
                header.createCell(3).setCellValue("GAJI");
                header.createCell(LABEL_START_COLUMN).setCellValue("POTONGAN");
                sheet.addMergedRegion(new CellRangeAddress(
                        HEADER_ROW, HEADER_ROW,
                        LABEL_START_COLUMN, LABEL_START_COLUMN + KATEGORI.size() - 1));
                header.createCell(LABEL_START_COLUMN + KATEGORI.size()).setCellValue("JUMLAH POTONGAN");
                header.createCell(LABEL_START_COLUMN + KATEGORI.size() + 1).setCellValue("GAJI BERSIH");

                // label kategori baris 10, kolom E..X
                Row label = sheet.createRow(LABEL_ROW);
                for (int i = 0; i < KATEGORI.size(); i++) {
                    label.createCell(LABEL_START_COLUMN + i).setCellValue(KATEGORI.get(i));
                }

                // pre-fill NO | NAMA | NIPAM | GAJI (gaji_pokok), sel potongan kosong
                int rowNum = FIRST_DATA_ROW;
                int no = 1;
                for (GajiBatchMaster m : entry.getValue()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(no++);
                    row.createCell(1).setCellValue(m.getNama() != null ? m.getNama() : "");
                    row.createCell(2).setCellValue(m.getNipam() != null ? m.getNipam() : "");
                    if (m.getGajiPokok() != null) {
                        row.createCell(3).setCellValue(m.getGajiPokok());
                    }
                }
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private String trimSheetName(String name) {
        return name.length() <= 31 ? name : name.substring(0, 31);
    }
}