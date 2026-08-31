package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.DukResponse;
import id.perumdamts.kepegawaian.repositories.laporan.kepegawaian.DukRepository;
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
public class DukService {
    private final DukRepository repository;

    public List<DukResponse> fetch() {
        return repository.fetch();
    }

    public ByteArrayResource exportExcel() {
        var data = fetch();
        try (var in = getClass().getResourceAsStream("/templates/laporan/template_duk.xlsx")) {
            var wb = new XSSFWorkbook(in);
            var ws = wb.getSheetAt(0);

            // Title: "BULAN : Agustus 2026" (matches Python)
            ExcelDateHelper.setTitle(ws.getRow(1), ws, ExcelDateHelper.bulanTitle(), 15);

            int rowNum = 6;
            for (int i = 0; i < data.size(); i++) {
                var r = data.get(i);
                var row = ws.createRow(rowNum++);
                writeRow(row, r, i + 1);
            }

            var out = new ByteArrayOutputStream();
            wb.write(out);
            wb.close();
            return new ByteArrayResource(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate DUK Excel", e);
        }
    }

    private void writeRow(Row row, DukResponse r, int idx) {
        ExcelDateHelper.writeStyledCell(row, 0, idx, "bold", "allborder");
        ExcelDateHelper.writeStyledCell(row, 1, r.nama(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 2, r.nipam(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 3, r.golongan(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 4, r.pangkat(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 5, ExcelDateHelper.formatDate(r.tmtGolongan()), "allborder");
        ExcelDateHelper.writeStyledCell(row, 6, r.namaJabatan(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 7, ExcelDateHelper.formatDate(r.tmtJabatan()), "allborder");
        ExcelDateHelper.writeStyledCell(row, 8, ExcelDateHelper.formatDate(r.tmtKerja()), "allborder");
        ExcelDateHelper.writeStyledCell(row, 9, r.mkTahun(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 10, hitungSisaBulan(r.mkTahun(), r.mkBulan()), "allborder");
        ExcelDateHelper.writeStyledCell(row, 11, r.jurusan(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 12, r.tahunLulus(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 13, r.tingkatPendidikan(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 14, r.usia(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 15, decodeStatusPegawai(r.statusPegawai()), "allborder");
    }

    private int hitungSisaBulan(Integer tahun, Integer bulan) {
        int t = tahun != null ? tahun : 0;
        int b = bulan != null ? bulan : 0;
        return Math.max(0, b - (t * 12));
    }

    private String decodeStatusPegawai(Byte b) {
        if (b == null) return "";
        return switch (b) {
            case 0 -> "Pegawai Kontrak";
            case 1 -> "Calon Pegawai";
            case 2 -> "Pegawai Tetap";
            case 3 -> "Calon Honorer Tetap";
            case 4 -> "Honorer Tetap";
            case 5 -> "Non Pegawai";
            default -> "Invalid";
        };
    }
}
