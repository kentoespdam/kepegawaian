package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.*;
import id.perumdamts.kepegawaian.repositories.laporan.kepegawaian.StatistikRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatistikService {
    private final StatistikRepository repository;

    public List<StatistikGolonganResponse> fetchGolongan() {
        return repository.fetchByGolongan();
    }

    public List<StatistikPendidikan1Response> fetchPendidikan1() {
        return repository.fetchByPendidikan1();
    }

    public List<StatistikPendidikan2Response> fetchPendidikan2(int tahun, int bulan) {
        return repository.fetchByPendidikan2(tahun, bulan);
    }

    public List<StatistikUmurResponse> fetchUmur() {
        return repository.fetchByUmur();
    }

    /** Combined response matching Python: {"umur": [...], "range": [...]} */
    public StatistikUmurCombinedResponse fetchUmurCombined() {
        return new StatistikUmurCombinedResponse(fetchUmur(), fetchUmurRange());
    }

    public List<StatistikUmurRangeResponse> fetchUmurRange() {
        var data = fetchUmur();
        var ranges = List.of("<20", "20-29", "30-39", "40-49", "50-59", ">60");
        var totals = new int[6];
        for (var r : data) {
            int age = r.umur();
            if (age < 20) totals[0] += r.total();
            else if (age < 30) totals[1] += r.total();
            else if (age < 40) totals[2] += r.total();
            else if (age < 50) totals[3] += r.total();
            else if (age < 60) totals[4] += r.total();
            else totals[5] += r.total();
        }
        int grandTotal = java.util.Arrays.stream(totals).sum();
        var result = new ArrayList<StatistikUmurRangeResponse>();
        for (int i = 0; i < 6; i++) {
            int t = totals[i];
            double pct = grandTotal > 0 ? Math.round(((double) t / grandTotal) * 10000.0) / 100.0 : 0;
            result.add(new StatistikUmurRangeResponse(ranges.get(i), t, pct));
        }
        return result;
    }

    public List<StatistikJenisKelaminResponse> fetchJenisKelamin() {
        return repository.fetchByJenisKelamin();
    }

    public List<StatistikGelarResponse> fetchGelar() {
        return repository.fetchByGelar();
    }

    public List<StatistikAgamaResponse> fetchAgama() {
        return repository.fetchByAgama();
    }

    public List<StatistikStatusPegawaiResponse> fetchStatusPegawai() {
        return repository.fetchByStatusPegawai();
    }

    public ByteArrayResource exportExcelPendidikan2(int tahun, int bulan) {
        var data = fetchPendidikan2(tahun, bulan);
        try (var in = getClass().getResourceAsStream("/templates/laporan/template_pendidikan_2.xlsx")) {
            var wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook(in);
            var ws = wb.getSheetAt(0);

            ExcelDateHelper.setTitle(ws.getRow(1), ws, ExcelDateHelper.bulanTitle(tahun, bulan), 18);

            int rowNum = 5;
            for (var r : data) {
                var row = ws.createRow(rowNum++);
                writeCell(row, 0, r.pendidikan());
                writeCell(row, 1, r.nonGolongan());
                writeCell(row, 2, r.golonganA());
                writeCell(row, 3, r.golonganB());
                writeCell(row, 4, r.golonganC());
                writeCell(row, 5, r.golonganD());
                writeCell(row, 6, r.jmlGolongan());
                writeCell(row, 7, r.kontrak());
                writeCell(row, 8, r.capeg());
                writeCell(row, 9, r.honorer());
                writeCell(row, 10, r.tetap());
                writeCell(row, 11, r.jmlStatusPegawai());
                writeCell(row, 12, r.adm());
                writeCell(row, 13, r.pelayanan());
                writeCell(row, 14, r.teknik());
                writeCell(row, 15, r.jmlUnitKerja());
                writeCell(row, 16, r.pria());
                writeCell(row, 17, r.wanita());
                writeCell(row, 18, r.jmlJenisKelamin());
            }

            var out = new java.io.ByteArrayOutputStream();
            wb.write(out);
            wb.close();
            return new ByteArrayResource(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Pendidikan2 Excel", e);
        }
    }

    private void writeCell(org.apache.poi.ss.usermodel.Row row, int col, Object value) {
        ExcelDateHelper.writeStyledCell(row, col, value, "allborder");
    }
}
