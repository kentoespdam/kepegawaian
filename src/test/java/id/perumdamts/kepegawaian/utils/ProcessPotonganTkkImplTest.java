package id.perumdamts.kepegawaian.utils;

import id.perumdamts.kepegawaian.entities.commons.EJenisPotonganGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchPotonganTkk;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootLampiran;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchPotonganTkkRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchRootLampiranRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchRootRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class ProcessPotonganTkkImplTest {
    private final static String FILE_PATH = System.getProperty("user.dir") + "/attachments/Penggajian/PotonganTKK/";
    private static final String TMP_FILE_NAME = "temp.xlsx";
    @Autowired
    private GajiBatchPotonganTkkRepository repository;
    private Sheet sheet;
    @Autowired
    private GajiBatchRootRepository gajiBatchRootRepository;
    @Autowired
    private ProcessPotonganTkk processPotonganTkk;
    @Autowired
    private GajiBatchRootLampiranRepository gajiBatchRootLampiranRepository;


    //    @Test
    @Transactional
    void process() {
        GajiBatchRoot gajiBatchRoot = gajiBatchRootRepository.findById("202401-003").orElse(null);
        assert gajiBatchRoot != null;
        List<GajiBatchRootLampiran> lampiran = gajiBatchRootLampiranRepository.findByGajiBatchRoot_IdAndJenisLampiranGaji(gajiBatchRoot.getId(), EJenisPotonganGaji.POTONGAN_TKK);
        assert lampiran != null;
        GajiBatchRootLampiran last = lampiran.stream().filter(l -> l.getJenisLampiranGaji().equals(EJenisPotonganGaji.POTONGAN_TKK)).toList().getLast();
        log.info("debugging: {}", last.getFileName());
        Sheet sheet1 = setSheetFile(gajiBatchRoot.getPeriode(), last);
        assert sheet1 != null;
        List<GajiBatchPotonganTkk> potonganTkkList = readingData(gajiBatchRoot.getId(), sheet1);
    }

    private Sheet setSheetFile(String periode, GajiBatchRootLampiran lampiran) {
        String filePath = FILE_PATH + periode + "/" + lampiran.getHashedFileName();
        String tmpPath = FILE_PATH + periode + "/" + TMP_FILE_NAME;

        try {
            Files.deleteIfExists(new File(tmpPath).toPath());
            Files.copy(new File(filePath).toPath(), new File(tmpPath).toPath());

            FileInputStream fileInputStream = new FileInputStream(tmpPath);
            if (lampiran.getMimeType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                return new XSSFWorkbook(fileInputStream).getSheetAt(0);
            else if (lampiran.getMimeType().equals("application/vnd.ms-excel")) {
                return new HSSFWorkbook(fileInputStream).getSheetAt(0);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private List<GajiBatchPotonganTkk> readingData(String rootBatchId, Sheet sheet1) {
        List<GajiBatchPotonganTkk> list = new ArrayList<>();
        for (int i = 4; i < sheet1.getPhysicalNumberOfRows(); i++) {
            GajiBatchPotonganTkk entity = new GajiBatchPotonganTkk();
            entity.setBatchId(rootBatchId);
            entity.setNipam(sheet1.getRow(i).getCell(1).getStringCellValue());
            entity.setPotongan((int) sheet1.getRow(i).getCell(3).getNumericCellValue());
            list.add(entity);
        }

        return list;
    }

    //    @Test
    void execute_proses() {
        processPotonganTkk.process("202401-004");
    }
}