package id.perumdamts.kepegawaian.utils;

import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchPotonganTkk;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchPotonganTkkRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessPotonganTkkImpl implements ProcessPotonganTkk {
    private final static String FILE_PATH = System.getProperty("user.dir") + "/attachments/Penggajian/PotonganTKK/";
    private static final String TMP_FILE_NAME = "temp.xlsx";
    private final GajiBatchPotonganTkkRepository repository;
    private Sheet sheet;


    private void setup(GajiBatchRoot params) {
        try {
            String filePath = FILE_PATH + params.getPeriode() + "/" + params.getHashedFileName();
            String tmpPath = FILE_PATH + params.getPeriode() + "/" + TMP_FILE_NAME;

            Files.deleteIfExists(new File(tmpPath).toPath());
            Files.copy(new File(filePath).toPath(), new File(tmpPath).toPath());

            FileInputStream fileInputStream = new FileInputStream(tmpPath);
            this.sheet = new XSSFWorkbook(fileInputStream).getSheetAt(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void process(GajiBatchRoot params) {
        if (params.getHashedFileName() == null) return;
        setup(params);
        String batchId = params.getBatchId();
        List<GajiBatchPotonganTkk> list = new ArrayList<>();
        for (int i = 4; i < sheet.getPhysicalNumberOfRows(); i++) {
            GajiBatchPotonganTkk entity = new GajiBatchPotonganTkk();
            entity.setBatchId(batchId);
            entity.setNipam(sheet.getRow(i).getCell(1).getStringCellValue());
            entity.setPotongan((int) sheet.getRow(i).getCell(3).getNumericCellValue());
            list.add(entity);
        }
        if (list.isEmpty()) return;
        repository.saveAll(list);
    }
}
