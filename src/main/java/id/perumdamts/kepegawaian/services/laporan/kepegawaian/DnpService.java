package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.DnpResponse;
import id.perumdamts.kepegawaian.repositories.laporan.kepegawaian.DnpRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DnpService {
    private final DnpRepository repository;

    public List<DnpResponse> fetch() {
        var data = repository.fetch();
        return cleanup(data);
    }

    private List<DnpResponse> cleanup(List<DnpResponse> data) {
        return data.stream().map(d -> {
            var mkBulan = d.mkBulan() != null ? d.mkBulan() : 0;
            var mkTahun = d.mkTahun() != null ? d.mkTahun() : 0;
            var mkgBulan = d.mkgBulan() != null ? d.mkgBulan() : 0;
            var mkgTahun = d.mkgTahun() != null ? d.mkgTahun() : 0;

            int cleanedMk = Math.max(0, mkBulan - (mkTahun * 12));
            int cleanedMkg = Math.max(0, mkgBulan - (mkgTahun * 12));

            String kode = d.kodeOrganisasi();
            if (d.levelJabatan() != null && (d.levelJabatan() == 2 || d.levelJabatan() == 3 || d.levelJabatan() == 4)) {
                kode = "1";
            }

            return new DnpResponse(
                    kode, d.levelJabatan(), d.nama(), d.nipam(), d.namaJabatan(),
                    d.tmtJabatan(), d.pangkat(), d.golongan(), d.tmtGolongan(),
                    mkgTahun, cleanedMkg, d.tmtKerja(), mkTahun, cleanedMk,
                    d.pendidikan(), d.ttl()
            );
        }).toList();
    }

    public List<Map<String, Object>> fetchOrganisasi() {
        var result = new ArrayList<Map<String, Object>>();
        result.add(Map.of("kode", "1", "nama", "DIREKSI"));
        var orgs = repository.fetchOrganisasiCodes(4);
        result.addAll(orgs);
        return result;
    }

    public ByteArrayResource exportExcel() {
        var data = fetch();
        var orgList = fetchOrganisasi();

        var grouped = new LinkedHashMap<String, List<DnpResponse>>();
        for (var org : orgList) {
            grouped.put((String) org.get("kode"), new ArrayList<>());
        }
        for (var d : data) {
            grouped.computeIfAbsent(d.kodeOrganisasi(), k -> new ArrayList<>()).add(d);
        }

        try (var in = getClass().getResourceAsStream("/templates/laporan/template_dnp.xlsx")) {
            var wb = new XSSFWorkbook(in);
            var ws = wb.getSheetAt(0);

            ExcelDateHelper.setTitle(ws.getRow(1), ws, ExcelDateHelper.bulanTitle(), 15);

            int rowNum = 7;
            int rootUrut = 1;
            for (var org : orgList) {
                String nama = (String) org.get("nama");
                var headerRow = ws.createRow(rowNum++);
                ExcelDateHelper.writeStyledCell(headerRow, 0, nama, "bold");
                ws.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(
                        rowNum - 1, rowNum - 1, 0, 15));

                String kode = (String) org.get("kode");
                var pegawaiList = grouped.getOrDefault(kode, List.of());
                int childUrut = 1;
                for (var p : pegawaiList) {
                    var row = ws.createRow(rowNum++);
                    writeRow(row, p, rootUrut++, childUrut++);
                }
                ws.createRow(rowNum++); // empty separator
            }

            var out = new ByteArrayOutputStream();
            wb.write(out);
            wb.close();
            return new ByteArrayResource(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate DNP Excel", e);
        }
    }

    private void writeRow(Row row, DnpResponse p, int rootUrut, int childUrut) {
        ExcelDateHelper.writeStyledCell(row, 0, rootUrut, "bold", "allborder");
        ExcelDateHelper.writeStyledCell(row, 1, childUrut, "allborder");
        ExcelDateHelper.writeStyledCell(row, 2, p.nama(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 3, p.nipam(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 4, p.namaJabatan(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 5, p.tmtJabatan(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 6, p.pangkat(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 7, p.golongan(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 8, p.tmtGolongan(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 9, p.mkgTahun(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 10, p.mkgBulan(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 11, p.tmtKerja(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 12, p.mkTahun(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 13, p.mkBulan(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 14, p.pendidikan(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 15, p.ttl(), "allborder");
    }
}
