package id.perumdamts.kepegawaian.services.cuti.kuota;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaImportRequest;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiIdNipam;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.cuti.CutiKuota;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiKuotaRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessCutiKuotaService {
    private final CutiKuotaRepository repository;
    private final PegawaiRepository pegawaiRepository;

    public SavedStatus<?> processCutiKuota(CutiKuotaImportRequest request) {
        Integer tahun = request.getTahun();
        MultipartFile file = request.getFile();

        boolean existByTahun = repository.existsByTahun(tahun);
        if (existByTahun)
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Kuota Cuti Tahun " + tahun + " sudah ada");
        Workbook workbook = getWorkbook(file);
        if (workbook == null)
            return SavedStatus.build(ESaveStatus.FAILED, "Gagal membaca file");
        List<CutiKuota> cutiKuotaList = readSheetData(workbook.getSheetAt(0), tahun);
        if (cutiKuotaList.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Tidak ada data");
        repository.saveAll(cutiKuotaList);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Kuota Cuti Tahun " + tahun + " berhasil disimpan");
    }

    private Workbook getWorkbook(MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if ("application/vnd.ms-excel".equals(contentType)) {
                return new HSSFWorkbook(file.getInputStream());
            } else if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType)) {
                return new XSSFWorkbook(file.getInputStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to process the spreadsheet file", e);
        }
        return null;
    }

    private List<CutiKuota> readSheetData(Sheet sheet, Integer tahun) {
        LocalDate expired = LocalDate.of(tahun + 1, 6, 30);
        List<CutiKuota> cutiKuotaList = new ArrayList<>();
        List<PegawaiIdNipam> listIdAndNipam = pegawaiRepository.findByStatusKerjaInAndStatusPegawai(List.of(EStatusKerja.DIRUMAHKAN, EStatusKerja.KARYAWAN_AKTIF), EStatusPegawai.PEGAWAI);

        for (int rowIndex = 1; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
            CutiKuota cutiKuota = new CutiKuota();

            String nipam = sheet.getRow(rowIndex).getCell(0).getStringCellValue();
            Integer kuota = (int) sheet.getRow(rowIndex).getCell(2).getNumericCellValue();
            Integer kuotaTerpakai = (int) sheet.getRow(rowIndex).getCell(3).getNumericCellValue();
            Integer sisaKuota = (int) sheet.getRow(rowIndex).getCell(4).getNumericCellValue();
            Pegawai pegawai = listIdAndNipam.stream()
                    .filter(p -> p.getNipam().equals(nipam))
                    .findFirst()
                    .map(r -> new Pegawai(r.getId())).orElse(null);

            if (pegawai == null) continue;

            cutiKuota.setPegawai(pegawai);
            cutiKuota.setTahun(tahun);
            cutiKuota.setKuota(kuota);
            cutiKuota.setKuotaTerpakai(kuotaTerpakai);
            cutiKuota.setSisaKuota(sisaKuota);
            cutiKuota.setExpired(expired);
            cutiKuotaList.add(cutiKuota);
        }

        return cutiKuotaList;
    }
}
