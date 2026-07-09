package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.helpers.UrlBuilder;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.LaporanKepegawaianService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/laporan/kepegawaian/statistik")
@RequiredArgsConstructor
public class LaporanStatistikController {
    private static final String BASE_PATH = "/statistik";
    private final LaporanKepegawaianService service;

    @GetMapping("/golongan")
    public ResponseEntity<SingleResult<Object>> lapStatistikGolongan() {
        return CustomResult.any(
                service.getObject(UrlBuilder.build(BASE_PATH, "/golongan")));
    }

    @GetMapping("/pendidikan1")
    public ResponseEntity<SingleResult<Object>> lapStatistikPendidikan1() {
        return CustomResult.any(
                service.getObject(UrlBuilder.build(BASE_PATH, "/pendidikan1")));
    }

    @GetMapping("/pendidikan2/{tahun}/{bulan}")
    public ResponseEntity<SingleResult<Object>> lapStatistikPendidikan2(@PathVariable int tahun, @PathVariable int bulan) {
        return CustomResult.any(
                service.getObject(UrlBuilder.build(BASE_PATH, "/pendidikan2?tahun=" + tahun + "&bulan=" + bulan)));
    }

    @GetMapping("/pendidikan2/excel/{tahun}/{bulan}")
    public ResponseEntity<?> lapStatistikPendidikan2Excel(@PathVariable int tahun, @PathVariable int bulan) {
        return service.getExport(
                UrlBuilder.build(BASE_PATH, "/pendidikan2/excel?tahun=" + tahun + "&bulan=" + bulan));
    }

    @GetMapping("/umur")
    public ResponseEntity<SingleResult<Object>> lapStatistikUmur() {
        return CustomResult.any(
                service.getObject(UrlBuilder.build(BASE_PATH, "/umur")));
    }

    @GetMapping("/jenis_kelamin")
    public ResponseEntity<SingleResult<Object>> lapStatistikJenisKelamin() {
        return CustomResult.any(
                service.getObject(UrlBuilder.build(BASE_PATH, "/jenis_kelamin")));
    }

    @GetMapping("/gelar_akademik")
    public ResponseEntity<SingleResult<Object>> lapStatistikGelarAkademik() {
        return CustomResult.any(
                service.getObject(UrlBuilder.build(BASE_PATH, "/gelar_akademik")));
    }

    @GetMapping("/agama")
    public ResponseEntity<SingleResult<Object>> lapStatistikAgama() {
        return CustomResult.any(
                service.getObject(UrlBuilder.build(BASE_PATH, "/agama")));
    }

    @GetMapping("/status_pegawai")
    public ResponseEntity<SingleResult<Object>> lapStatistikStatusPegawai() {
        return CustomResult.any(
                service.getObject(UrlBuilder.build(BASE_PATH, "/status_pegawai")));
    }
}
