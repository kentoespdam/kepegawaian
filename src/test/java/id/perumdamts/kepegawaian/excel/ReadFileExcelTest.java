package id.perumdamts.kepegawaian.excel;

import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchPotonganTkk;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Slf4j
public class ReadFileExcelTest {
    private final static String FILE_PATH = System.getProperty("user.dir") + "/attachments/Penggajian/PotonganTKK/";
    private static final String FILE_NAME = "test.xlsx";
    private static final String TMP_FILE_NAME = "temp.xlsx";
    private Sheet sheet;

    @BeforeEach
    public void setup() {
        try {
            Files.deleteIfExists(new File(FILE_PATH + TMP_FILE_NAME).toPath());
            FileUtils.copyFile(new File(FILE_PATH + FILE_NAME), new File(FILE_PATH + TMP_FILE_NAME));
            FileInputStream fileInputStream = new FileInputStream(FILE_PATH + TMP_FILE_NAME);
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            sheet = workbook.getSheetAt(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    @Test
    public void test() {
        String batchId = "202402-001";
        List<GajiBatchPotonganTkk> list = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() <= 3) continue;
            GajiBatchPotonganTkk entity = new GajiBatchPotonganTkk();
            entity.setBatchId(batchId);
            entity.setNipam(row.getCell(1).getStringCellValue());
            entity.setPotongan((int) row.getCell(3).getNumericCellValue());
            list.add(entity);
        }
        log.info("list: {}", list);
    }
}
