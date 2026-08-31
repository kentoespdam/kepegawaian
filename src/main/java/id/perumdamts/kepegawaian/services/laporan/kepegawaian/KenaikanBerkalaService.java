package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EFilterKenaikanBerkala;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EJenisKenaikanBerkala;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.KenaikanBerkalaResponse;
import id.perumdamts.kepegawaian.repositories.laporan.kepegawaian.KenaikanBerkalaRepository;
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
public class KenaikanBerkalaService {
    private final KenaikanBerkalaRepository repository;

    public List<KenaikanBerkalaResponse> fetch(EFilterKenaikanBerkala filter, EJenisKenaikanBerkala jenisSk) {
        var data = repository.fetch(filter, jenisSk);
        return cleanup(data, jenisSk);
    }

    public Long count(EFilterKenaikanBerkala filter, EJenisKenaikanBerkala jenisSk) {
        return repository.count(filter, jenisSk);
    }

    private List<KenaikanBerkalaResponse> cleanup(List<KenaikanBerkalaResponse> data, EJenisKenaikanBerkala jenisSk) {
        return data.stream().map(r -> {
            var tanggalEksekusi = cleanupExecutionDate(jenisSk, r.tanggalEksekusiSanksi(), r.isPendingGaji(), r.isPendingPangkat());
            var mkgBulan = r.mkgBulan() != null ? Math.max(0, r.mkgBulan()) : 0;
            var mkgTahun = r.mkgTahun() != null ? Math.max(0, r.mkgTahun()) : 0;
            var mkBulan = r.mkBulan() != null ? Math.max(0, r.mkBulan()) : 0;
            var mkTahun = r.mkTahun() != null ? Math.max(0, r.mkTahun()) : 0;

            return new KenaikanBerkalaResponse(
                    r.id(), r.pegawaiId(), r.nipam(), r.nama(),
                    r.jenisSk(), r.nomorSk(), r.tmtBerlaku(), r.kenaikanBerikutnya(),
                    tanggalEksekusi, r.isPendingGaji(), r.isPendingPangkat(),
                    r.namaJabatan(), r.tmtJabatan(),
                    r.golongan(), r.pangkat(), r.tmtGolongan(),
                    mkgTahun, mkgBulan, r.tmtKerja(),
                    mkTahun, mkBulan,
                    r.pendidikanTerakhir(), r.tempatLahir(), r.tanggalLahir()
            );
        }).toList();
    }

    private LocalDate cleanupExecutionDate(EJenisKenaikanBerkala jenisSk,
                                           LocalDate tanggalEksekusi,
                                           boolean isPendingGaji,
                                           boolean isPendingPangkat) {
        boolean shouldNullify = switch (jenisSk) {
            case SK_KENAIKAN_GAJI_BERKALA -> !isPendingGaji;
            case SK_KENAIKAN_PANGKAT_GOLONGAN -> !isPendingPangkat;
        };
        return shouldNullify ? null : tanggalEksekusi;
    }

    public ByteArrayResource exportExcel(EFilterKenaikanBerkala filter, EJenisKenaikanBerkala jenisSk) {
        var data = fetch(filter, jenisSk);
        try (var in = getClass().getResourceAsStream("/templates/laporan/template_kenaikan_berkala.xlsx")) {
            var wb = new XSSFWorkbook(in);
            var ws = wb.getSheetAt(0);

            ExcelDateHelper.setTitle(ws.getRow(1), ws, ExcelDateHelper.bulanColonTitle(), 13);

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
            throw new RuntimeException("Failed to generate Kenaikan Berkala Excel", e);
        }
    }

    private void writeRow(Row row, KenaikanBerkalaResponse r, int idx) {
        ExcelDateHelper.writeStyledCell(row, 0, idx, "bold", "allborder");
        ExcelDateHelper.writeStyledCell(row, 1, decodeJenisSk(r.jenisSk()), "allborder");
        ExcelDateHelper.writeStyledCell(row, 2, r.nama(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 3, r.nipam(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 4, r.namaJabatan(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 5, ExcelDateHelper.formatDate(r.tmtJabatan()), "allborder");
        ExcelDateHelper.writeStyledCell(row, 6, r.pangkat(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 7, r.golongan(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 8, ExcelDateHelper.formatDate(r.tmtGolongan()), "allborder");
        ExcelDateHelper.writeStyledCell(row, 9, r.mkgTahun(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 10, r.mkgBulan(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 11, ExcelDateHelper.formatDate(r.tmtKerja()), "allborder");
        ExcelDateHelper.writeStyledCell(row, 12, r.mkTahun(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 13, r.mkBulan(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 14, r.pendidikanTerakhir(), "allborder");
        ExcelDateHelper.writeStyledCell(row, 15, r.tempatLahir() + ", " + ExcelDateHelper.formatDate(r.tanggalLahir()), "allborder");
        ExcelDateHelper.writeStyledCell(row, 16, "", "allborder");
    }

    private String decodeJenisSk(Byte b) {
        if (b == null) return "";
        return switch (b) {
            case 0 -> "SK Kenaikan Pangkat/Gol";
            case 1 -> "SK Calon Pegawai";
            case 2 -> "SK Pegawai Tetap";
            case 3 -> "SK Jabatan";
            case 4 -> "SK Mutasi Lokasi Kerja";
            case 5 -> "SK Pensiun";
            case 6 -> "SK Lainnya";
            case 7 -> "SK Penyesuaian Gaji";
            case 8 -> "SK Kenaikan Gaji Berkala";
            default -> "Invalid";
        };
    }
}
