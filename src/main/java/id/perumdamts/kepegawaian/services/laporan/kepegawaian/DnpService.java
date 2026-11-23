package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.DnpResponse;
import id.perumdamts.kepegawaian.repositories.laporan.kepegawaian.DnpRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DnpService {
  private final DnpRepository repository;
  private final DnpExcelExporter excelExporter;

  public List<DnpResponse> fetch() {
    var data = repository.fetch();
    return cleanup(data);
  }

  private List<DnpResponse> cleanup(List<DnpResponse> data) {
    return data.stream()
        .map(
            d -> {
              Integer mkgTahun = d.mkgTahun();
              Integer cleanedMkg = null;
              if (d.mkgBulan() != null || d.mkgTahun() != null) {
                int mb = d.mkgBulan() != null ? d.mkgBulan() : 0;
                int mt = d.mkgTahun() != null ? d.mkgTahun() : 0;
                mkgTahun = mt;
                cleanedMkg = Math.max(0, mb - (mt * 12));
              }

              Integer mkTahun = d.mkTahun();
              Integer cleanedMk = null;
              if (d.mkBulan() != null || d.mkTahun() != null) {
                int mb = d.mkBulan() != null ? d.mkBulan() : 0;
                int mt = d.mkTahun() != null ? d.mkTahun() : 0;
                mkTahun = mt;
                cleanedMk = Math.max(0, mb - (mt * 12));
              }

              String kode = d.kodeOrganisasi();
              String orgGroup = d.orgGroup();
              if (d.levelJabatan() != null
                  && (d.levelJabatan() == 2 || d.levelJabatan() == 3 || d.levelJabatan() == 4)) {
                kode = "1";
                if (orgGroup == null || orgGroup.isBlank()) {
                  orgGroup = "01.DIREKSI";
                }
              }

              return new DnpResponse(
                  kode,
                  orgGroup,
                  d.levelJabatan(),
                  d.nama(),
                  d.nipam(),
                  d.namaJabatan(),
                  d.tmtJabatan(),
                  d.pangkat(),
                  d.golongan(),
                  d.tmtGolongan(),
                  mkgTahun,
                  cleanedMkg,
                  d.tmtKerja(),
                  mkTahun,
                  cleanedMk,
                  d.pendidikan(),
                  d.ttl());
            })
        .toList();
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
    return excelExporter.export(data);
  }
}
