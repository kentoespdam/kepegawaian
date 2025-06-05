package id.perumdamts.kepegawaian.services.cuti.kuota;

import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.helpers.ExcelHelper;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class CutiKuotaTemplateBuilder {
    private final PegawaiRepository pegawaiRepository;

    public ResponseEntity<?> build() {
        Specification<Pegawai> pegawaiSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.in(root.get("statusKerja")).value(List.of(EStatusKerja.DIRUMAHKAN, EStatusKerja.KARYAWAN_AKTIF)),
                        criteriaBuilder.in(root.get("statusPegawai")).value(List.of(EStatusPegawai.PEGAWAI, EStatusPegawai.HONORER))
                );
        List<Pegawai> listPegawai = pegawaiRepository.findAll(pegawaiSpec);
        ByteArrayResource byteArrayResource = buildWorkbook(listPegawai);
        if (byteArrayResource == null)
            return ResponseEntity.notFound().build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Disposition", "attachment; filename=\"kuota_cuti.xlsx\"");
        httpHeaders.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        httpHeaders.add("Access-Control-Expose-Headers", "Content-Disposition");
        return ResponseEntity.ok()
                .headers(httpHeaders)
                .contentLength(byteArrayResource.contentLength())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(byteArrayResource);
    }

    private ByteArrayResource buildWorkbook(List<Pegawai> pegawaiList) {
        try {
            SXSSFWorkbook workbook = new SXSSFWorkbook();
            SXSSFSheet sheet = workbook.createSheet();
            sheet.trackColumnForAutoSizing(1);
            sheet.setRandomAccessWindowSize(100);

            writeHeader(sheet);
            insertColumn(sheet, pegawaiList);

            ByteArrayResource byteArrayResource = ExcelHelper.workbookToResource(workbook);
            workbook.close();
            return byteArrayResource;

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private void writeHeader(SXSSFSheet sheet) {
        SXSSFRow row = sheet.createRow(0);
        CellStyle cellStyle = ExcelHelper.createStyle(row, new String[]{"allBorder", "bold", "hCenter"});
        ExcelHelper.createCell(row, 0, "NIPAM", cellStyle);
        ExcelHelper.createCell(row, 1, "NAMA", cellStyle);
        ExcelHelper.createCell(row, 2, "Kuota", cellStyle);
        ExcelHelper.createCell(row, 3, "Terpakai", cellStyle);
        ExcelHelper.createCell(row, 4, "Sisa", cellStyle);
    }

    private void insertColumn(SXSSFSheet sheet, List<Pegawai> pegawaiList) {
        AtomicInteger rowNum = new AtomicInteger(1);
        CellStyle cellStyle = null;
        for (Pegawai pegawai : pegawaiList) {
            SXSSFRow row = sheet.createRow(rowNum.getAndIncrement());
            cellStyle = cellStyle == null ? ExcelHelper.createStyle(row, new String[]{"allBorder"}) : cellStyle;
            writeRow(row, pegawai, cellStyle);
        }
    }

    private void writeRow(SXSSFRow row, Pegawai pegawai, CellStyle cellStyle) {
        AtomicInteger colNum = new AtomicInteger(0);
        ExcelHelper.createCell(row, colNum.getAndIncrement(), pegawai.getNipam(), cellStyle);
        ExcelHelper.createCell(row, colNum.getAndIncrement(), pegawai.getBiodata().getNama(), cellStyle);
        ExcelHelper.createCell(row, colNum.getAndIncrement(), 12, cellStyle);
        ExcelHelper.createCell(row, colNum.getAndIncrement(), 0, cellStyle);
        ExcelHelper.createCell(row, colNum.getAndIncrement(), 12, cellStyle);
    }
}
