package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EFilterLta;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.LtaCountResponse;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.LtaResponse;
import id.perumdamts.kepegawaian.repositories.laporan.kepegawaian.LtaRepository;
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
public class LtaService {
    private final LtaRepository repository;

    public List<LtaResponse> fetch(EFilterLta filter) {
        return repository.fetch(filter);
    }

    public LtaCountResponse count(EFilterLta filter) {
        return new LtaCountResponse(repository.count(filter));
    }

    public ByteArrayResource exportExcel(EFilterLta filter) {
        var data = fetch(filter);
        try (var in = getClass().getResourceAsStream("/templates/laporan/template_lta.xlsx")) {
            var wb = new XSSFWorkbook(in);
            var ws = wb.getSheetAt(0);

            ExcelDateHelper.setTitle(ws.getRow(1), ws, ExcelDateHelper.bulanColonTitle(), 8);

            int rowNum = 4;
            for (int i = 0; i < data.size(); i++) {
                var r = data.get(i);
                var row = ws.createRow(rowNum++);
                ExcelDateHelper.writeStyledCell(row, 0, i + 1, "bold", "allborder");
                ExcelDateHelper.writeStyledCell(row, 1, r.namaAnak(), "allborder");
                ExcelDateHelper.writeStyledCell(row, 2, r.jenisKelamin(), "allborder");
                ExcelDateHelper.writeStyledCell(row, 3, ExcelDateHelper.formatDate(r.tanggalLahir()), "allborder");
                ExcelDateHelper.writeStyledCell(row, 4, r.umur(), "allborder");
                ExcelDateHelper.writeStyledCell(row, 5, r.statusPendidikan(), "allborder");
                ExcelDateHelper.writeStyledCell(row, 6, r.namaKaryawan(), "allborder");
                ExcelDateHelper.writeStyledCell(row, 7, r.nipam(), "allborder");
                ExcelDateHelper.writeStyledCell(row, 8, r.namaJabatan(), "allborder");
            }

            var out = new ByteArrayOutputStream();
            wb.write(out);
            wb.close();
            return new ByteArrayResource(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate LTA Excel", e);
        }
    }
}
