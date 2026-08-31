package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.MutasiResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import id.perumdamts.kepegawaian.repositories.laporan.kepegawaian.MutasiRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MutasiService {
    private final MutasiRepository repository;

    public List<MutasiResponse> fetch(LocalDate fromDate, LocalDate toDate, EJenisMutasi jenisMutasi) {
        return repository.fetch(fromDate, toDate, jenisMutasi);
    }

    public ByteArrayResource exportExcel(LocalDate fromDate, LocalDate toDate, EJenisMutasi jenisMutasi) {
        var data = fetch(fromDate, toDate, jenisMutasi);
        try (var in = getClass().getResourceAsStream("/templates/laporan/template_mutasi.xlsx")) {
            var wb = new XSSFWorkbook(in);
            var ws = wb.getSheetAt(0);

            ExcelDateHelper.setTitle(ws.getRow(1), ws,
                    ExcelDateHelper.mutasiTitle(fromDate, toDate), 11);

            int rowNum = 4;
            for (int i = 0; i < data.size(); i++) {
                var r = data.get(i);
                var row = ws.createRow(rowNum++);
                ExcelDateHelper.writeStyledCell(row, 0, r.jenisMutasi() != null ? r.jenisMutasi().value : "", "bold", "allborder");
                ExcelDateHelper.writeStyledCell(row, 1, r.nipam(), "allborder");
                ExcelDateHelper.writeStyledCell(row, 2, r.nama(), "allborder");
                ExcelDateHelper.writeStyledCell(row, 3, ExcelDateHelper.formatDate(r.tmtBerlaku()), "allborder");
                ExcelDateHelper.writeStyledCell(row, 4, r.namaOrganisasiLama(), "allborder");
                ExcelDateHelper.writeStyledCell(row, 5, r.namaJabatanLama(), "allborder");
                ExcelDateHelper.writeStyledCell(row, 6, r.namaGolonganLama(), "allborder");
                ExcelDateHelper.writeStyledCell(row, 7, r.namaOrganisasi(), "allborder");
                ExcelDateHelper.writeStyledCell(row, 8, r.namaJabatan(), "allborder");
                ExcelDateHelper.writeStyledCell(row, 9, r.namaGolongan(), "allborder");
                ExcelDateHelper.writeStyledCell(row, 10, r.notes(), "allborder");
            }

            var out = new ByteArrayOutputStream();
            wb.write(out);
            wb.close();
            return new ByteArrayResource(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Mutasi Excel", e);
        }
    }
}
