package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;


import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.LaporanKepegawaianService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/laporan/kepegawaian/mutasi")
@RequiredArgsConstructor
public class LaporanMutasiController {
    private static final String BASE_PATH = "/mutasi";
    private final LaporanKepegawaianService service;

    @GetMapping("/{from_date}/{to_date}")
    public ResponseEntity<SingleResult<Object>> lapMutasi(@PathVariable String from_date, @PathVariable String to_date, @RequestParam(required = false) EJenisMutasi jenis_mutasi) {
        return CustomResult.any(service.getObject(urlBuilder("/", from_date, to_date, jenis_mutasi)));
    }

    @GetMapping("/excel/{from_date}/{to_date}")
    public ResponseEntity<?> lapMutasiExcel(@PathVariable String from_date, @PathVariable String to_date, @RequestParam(required = false) EJenisMutasi jenis_mutasi) {
        return service.getExport(urlBuilder("/excel", from_date, to_date, jenis_mutasi));
    }

    private String urlBuilder(String endpoint, String fromDate, String toDate, EJenisMutasi jenisMutasi) {
        StringBuilder url = new StringBuilder(BASE_PATH);
        url.append(endpoint);
        url.append("/").append(fromDate);
        url.append("/").append(toDate);
        StringBuilder query = new StringBuilder();
        if (jenisMutasi != null) {
            query.append("?jenis_mutasi=").append(jenisMutasi.name());
        }
        String result = url.append(query).toString();
        System.out.println(result);
        return result;
    }
}
