package id.perumdamts.kepegawaian.utils;

import id.perumdamts.kepegawaian.entities.commons.EJenisPotonganGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchPotonganTkk;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootLampiran;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchPotonganTkkRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchRootLampiranRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
@Slf4j
public class ProcessPotonganTkkImpl implements ProcessPotonganTkk {
    private final static String FILE_PATH = System.getProperty("user.dir") + "/attachments/Penggajian/PotonganTKK/";
    private static final String TMP_FILE_NAME = "temp.xlsx";
    private final GajiBatchPotonganTkkRepository repository;
    private final GajiBatchRootLampiranRepository gajiBatchRootLampiranRepository;

    @Override
    public void process(String rootBatchId) {
        log.info("Starting Process Potongan TKK, {}", rootBatchId);
        List<GajiBatchRootLampiran> list = gajiBatchRootLampiranRepository.findByGajiBatchRoot_IdAndJenisLampiranGaji(rootBatchId, EJenisPotonganGaji.POTONGAN_TKK);
        list.sort((l1, l2) -> l2.getId().compareTo(l1.getId()));
        GajiBatchRootLampiran last = list.getLast();
        if (last == null) return;
        Workbook workbook = getWorkbook(last.getGajiBatchRoot().getPeriode(), last);
        if (workbook == null) return;
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) return;
        List<GajiBatchPotonganTkk> data = readSheetData(rootBatchId, sheet);
        log.info("debugging: {}", data.size());
        if (data.isEmpty()) return;
        repository.saveAll(data);

    }

    private Workbook getWorkbook(String period, GajiBatchRootLampiran attachment) {
        String originalFilePath = FILE_PATH + period + "/" + attachment.getHashedFileName();
        String temporaryFilePath = FILE_PATH + period + "/" + TMP_FILE_NAME;

        try {
            Files.deleteIfExists(new File(temporaryFilePath).toPath());
            Files.copy(new File(originalFilePath).toPath(), new File(temporaryFilePath).toPath());

            try (FileInputStream fileInputStream = new FileInputStream(temporaryFilePath)) {
                if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(attachment.getMimeType())) {
                    return new XSSFWorkbook(fileInputStream);
                } else if ("application/vnd.ms-excel".equals(attachment.getMimeType())) {
                    return new HSSFWorkbook(fileInputStream);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to process the spreadsheet file", e);
        }
        return null;
    }

    private List<GajiBatchPotonganTkk> readSheetData(String batchId, Sheet sheet) {
        List<GajiBatchPotonganTkk> potonganTkkList = new ArrayList<>();

        for (int rowIndex = 4; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
            GajiBatchPotonganTkk potonganTkk = new GajiBatchPotonganTkk();

            potonganTkk.setBatchId(batchId);
            Row row = sheet.getRow(rowIndex);
            String nipam = row.getCell(1).getStringCellValue();
            if (nipam == null || nipam.isEmpty()) continue;
            potonganTkk.setNipam(nipam);
            potonganTkk.setPotongan((int) row.getCell(3).getNumericCellValue());

            potonganTkkList.add(potonganTkk);
        }

        return potonganTkkList;
    }
}
