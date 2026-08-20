package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.LaporanKepegawaianService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Laporan — Laporan Statistik")
@RestController
@RequestMapping("/laporan/kepegawaian/statistik")
@RequiredArgsConstructor
public class LaporanStatistikController {
    private static final String BASE_PATH = "/statistik";
    private final LaporanKepegawaianService service;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap statistik golongan")
    @GetMapping("/golongan")
    public ResponseEntity<SingleResult<Object>> lapStatistikGolongan() {
        return CustomResult.any(
                service.getObject(BASE_PATH + "/golongan"));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap statistik pendidikan1")
    @GetMapping("/pendidikan1")
    public ResponseEntity<SingleResult<Object>> lapStatistikPendidikan1() {
        return CustomResult.any(
                service.getObject(BASE_PATH + "/pendidikan1"));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap statistik pendidikan2")
    @GetMapping("/pendidikan2/{tahun}/{bulan}")
    public ResponseEntity<SingleResult<Object>> lapStatistikPendidikan2(@PathVariable int tahun, @PathVariable int bulan) {
        return CustomResult.any(
                service.getObject(UriComponentsBuilder.fromPath(BASE_PATH).path("/pendidikan2")
                        .queryParam("tahun", tahun).queryParam("bulan", bulan).toUriString()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap statistik pendidikan2 excel")
    @GetMapping("/pendidikan2/excel/{tahun}/{bulan}")
    public ResponseEntity<?> lapStatistikPendidikan2Excel(@PathVariable int tahun, @PathVariable int bulan) {
        return service.getExport(
                UriComponentsBuilder.fromPath(BASE_PATH).path("/pendidikan2/excel")
                        .queryParam("tahun", tahun).queryParam("bulan", bulan).toUriString());
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap statistik umur")
    @GetMapping("/umur")
    public ResponseEntity<SingleResult<Object>> lapStatistikUmur() {
        return CustomResult.any(
                service.getObject(BASE_PATH + "/umur"));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap statistik jenis kelamin")
    @GetMapping("/jenis_kelamin")
    public ResponseEntity<SingleResult<Object>> lapStatistikJenisKelamin() {
        return CustomResult.any(
                service.getObject(BASE_PATH + "/jenis_kelamin"));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap statistik gelar akademik")
    @GetMapping("/gelar_akademik")
    public ResponseEntity<SingleResult<Object>> lapStatistikGelarAkademik() {
        return CustomResult.any(
                service.getObject(BASE_PATH + "/gelar_akademik"));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap statistik agama")
    @GetMapping("/agama")
    public ResponseEntity<SingleResult<Object>> lapStatistikAgama() {
        return CustomResult.any(
                service.getObject(BASE_PATH + "/agama"));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap statistik status pegawai")
    @GetMapping("/status_pegawai")
    public ResponseEntity<SingleResult<Object>> lapStatistikStatusPegawai() {
        return CustomResult.any(
                service.getObject(BASE_PATH + "/status_pegawai"));
    }
}
