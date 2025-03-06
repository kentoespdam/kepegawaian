package id.perumdamts.kepegawaian.excel;

import id.perumdamts.kepegawaian.entities.commons.EJenisPotonganGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootLampiran;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchRootLampiranRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Slf4j
@ActiveProfiles(value = "test")
public class ReadPotonganTambahanTest {
    @Autowired
    private GajiBatchRootLampiranRepository repository;
    private final static String FILE_PATH = System.getProperty("user.dir") + "/attachments/Penggajian/potongan/tambahan/";

    @Test
    public void test() {
        List<GajiBatchRootLampiran> list = repository.findByGajiBatchRoot_IdAndJenisLampiranGaji("202401-001", EJenisPotonganGaji.POTONGAN_TAMBAHAN);
        if (list.isEmpty()) return;
        GajiBatchRootLampiran last = list.getLast();
        try {
            Workbook workbook = readWorkbook(last);
//            workbook.sheetIterator().forEachRemaining(sheet -> readDataPerSheet(sheet));
            readDataPerSheet(workbook.getSheetAt(0));
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Workbook readWorkbook(GajiBatchRootLampiran attachment) {
        String filePath = FILE_PATH + attachment.getGajiBatchRoot().getPeriode() + "/" + attachment.getHashedFileName();
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            return attachment.getMimeType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    ? new XSSFWorkbook(fileInputStream)
                    : new HSSFWorkbook(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readDataPerSheet(Sheet sheet) {
        // Skip sheets that start with "Sheet"
        if (sheet.getSheetName().startsWith("Sheet")) return;

        List<String> headers = getHeaders(sheet);
        log.info("headers: {}", headers);

        // Iterate over all rows in the sheet
//        for (int rowIndex = 10; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
//            Row row = sheet.getRow(rowIndex);
//            if (row == null) continue;
//
//            Cell cell = row.getCell(0);
//            // Skip blank cells
//            if (cell == null || cell.getCellType() == CellType.BLANK) continue;
//
//        }
    }

    private List<String> getHeaders(Sheet sheet) {
        List<String> headers = new ArrayList<>();
        Row headerRow = sheet.getRow(9);
        for (Cell cell : headerRow) {
            headers.add(getStrValue(cell));
        }
        return headers;
    }

    private String getStrValue(Cell cell) {
        if (cell == null) return null;
        CellType cellType = cell.getCellType();
        String cellValue = null;
        if (cellType == CellType.STRING) {
            cellValue = cell.getStringCellValue();
        } else if (cellType == CellType.NUMERIC) {
            cellValue = String.valueOf((int) cell.getNumericCellValue());
        }
        return cellValue;
    }
}
