package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EFilterKontrak;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.KontrakResponse;
import id.perumdamts.kepegawaian.repositories.laporan.kepegawaian.KontrakRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KontrakService {
    private final KontrakRepository repository;

    public List<KontrakResponse> fetch(EFilterKontrak filter) {
        return repository.fetch(filter);
    }

    public ByteArrayResource exportExcel(EFilterKontrak filter) {
        var data = fetch(filter);
        try (var in = getClass().getResourceAsStream("/templates/laporan/template_kontrak.xlsx")) {
            var wb = new XSSFWorkbook(in);
            var ws = wb.getSheetAt(0);

            ExcelDateHelper.setTitle(ws.getRow(1), ws, ExcelDateHelper.kontrakTitle(filter), 8);

            int rowNum = 4;
            for (int i = 0; i < data.size(); i++) {
                var r = data.get(i);
                var row = ws.createRow(rowNum++);
                ExcelDateHelper.writeStyledCell(row, 0, i + 1, "bold", "allborder");
                ExcelDateHelper.writeStyledCell(row, 1, r.nipam(), "allborder");
                ExcelDateHelper.writeStyledCell(row, 2, r.nama(), "allborder");
                ExcelDateHelper.writeStyledCell(row, 3, r.nomorKontrak(), "allborder");
                ExcelDateHelper.writeStyledCell(row, 4, r.namaOrganisasi(), "allborder");
                ExcelDateHelper.writeStyledCell(row, 5, r.namaJabatan(), "allborder");
                ExcelDateHelper.writeStyledCell(row, 6, ExcelDateHelper.formatDate(r.tanggalMulai()), "allborder");
                ExcelDateHelper.writeStyledCell(row, 7, ExcelDateHelper.formatDate(r.tanggalSelesai()), "allborder");
                ExcelDateHelper.writeStyledCell(row, 8, r.sisaBulan(), "allborder");
            }

            var out = new ByteArrayOutputStream();
            wb.write(out);
            wb.close();
            return new ByteArrayResource(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Kontrak Excel", e);
        }
    }
}
