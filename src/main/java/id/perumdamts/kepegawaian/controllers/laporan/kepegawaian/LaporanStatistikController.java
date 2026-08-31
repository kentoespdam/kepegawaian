package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.*;
import java.util.List;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.StatistikService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Laporan — Laporan Statistik")
@RestController
@RequestMapping("/laporan/kepegawaian/statistik")
@RequiredArgsConstructor
public class LaporanStatistikController {
    private final StatistikService service;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap statistik golongan")
    @GetMapping("/golongan")
    public ResponseEntity<SingleResult<List<StatistikGolonganResponse>>> lapStatistikGolongan() {
        return CustomResult.any(service.fetchGolongan());
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap statistik pendidikan1")
    @GetMapping("/pendidikan1")
    public ResponseEntity<SingleResult<List<StatistikPendidikan1Response>>> lapStatistikPendidikan1() {
        return CustomResult.any(service.fetchPendidikan1());
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap statistik pendidikan2")
    @GetMapping("/pendidikan2/{tahun}/{bulan}")
    public ResponseEntity<SingleResult<List<StatistikPendidikan2Response>>> lapStatistikPendidikan2(
            @PathVariable int tahun, @PathVariable int bulan) {
        return CustomResult.any(service.fetchPendidikan2(tahun, bulan));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap statistik pendidikan2 excel")
    @GetMapping("/pendidikan2/excel/{tahun}/{bulan}")
    public ResponseEntity<?> lapStatistikPendidikan2Excel(
            @PathVariable int tahun, @PathVariable int bulan) {
        return ResponseEntity.ok(service.exportExcelPendidikan2(tahun, bulan));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap statistik umur")
    @GetMapping("/umur")
    public ResponseEntity<SingleResult<StatistikUmurCombinedResponse>> lapStatistikUmur() {
        return CustomResult.any(service.fetchUmurCombined());
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap statistik jenis kelamin")
    @GetMapping("/jenis_kelamin")
    public ResponseEntity<SingleResult<List<StatistikJenisKelaminResponse>>> lapStatistikJenisKelamin() {
        return CustomResult.any(service.fetchJenisKelamin());
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap statistik gelar akademik")
    @GetMapping("/gelar_akademik")
    public ResponseEntity<SingleResult<List<StatistikGelarResponse>>> lapStatistikGelarAkademik() {
        return CustomResult.any(service.fetchGelar());
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap statistik agama")
    @GetMapping("/agama")
    public ResponseEntity<SingleResult<List<StatistikAgamaResponse>>> lapStatistikAgama() {
        return CustomResult.any(service.fetchAgama());
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap statistik status pegawai")
    @GetMapping("/status_pegawai")
    public ResponseEntity<SingleResult<List<StatistikStatusPegawaiResponse>>> lapStatistikStatusPegawai() {
        return CustomResult.any(service.fetchStatusPegawai());
    }
}
