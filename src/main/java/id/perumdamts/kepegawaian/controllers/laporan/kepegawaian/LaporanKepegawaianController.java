package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EFilterKenaikanBerkala;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EFilterKontrak;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EFilterLta;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.LaporanKepegawaianService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/*
*
Daftar Urut Kepegawaian
Daftar Nominatif Pegawai
Struktur Organisasi
Statistik Pegawai
Cuti Pegawai
Mutasi Karyawan
Monitoring Kontrak
Daftar Anak Lepas Tanggungan
Daftar Kenaikan Gaji/Pangkat Berkala
Daftar Pensiun
* */

@RestController
@RequestMapping("/laporan/kepegawaian")
@RequiredArgsConstructor
@Slf4j
public class LaporanKepegawaianController {
    private final LaporanKepegawaianService service;

    @GetMapping("/duk")
    public ResponseEntity<?> lapDuk() {
        return CustomResult.any(service.getObject("/duk/"));
    }

    @GetMapping("/duk/excel")
    public ResponseEntity<?> lapDukExcel() {
        return service.getExport("/duk/excel");
    }

    @GetMapping("/dnp")
    public ResponseEntity<?> lapDnp() {
        return CustomResult.any(service.getObject("/dnp/"));
    }

    @GetMapping("/dnp/excel")
    public ResponseEntity<?> lapDnpExcel() {
        return service.getExport("/dnp/excel");
    }

    @GetMapping("/so")
    public ResponseEntity<?> lapSO() {
        return CustomResult.any(service.getObject("/so/"));
    }

    @GetMapping("/statistik/golongan")
    public ResponseEntity<?> lapStatistikGolongan() {
        return CustomResult.any(service.getObject("/statistik/golongan"));
    }

    @GetMapping("/statistik/pendidikan1")
    public ResponseEntity<?> lapStatistikPendidikan1() {
        return CustomResult.any(service.getObject("/statistik/pendidikan1"));
    }

    @GetMapping("/statistik/pendidikan2/{tahun}/{bulan}")
    public ResponseEntity<?> lapStatistikPendidikan2(@PathVariable int tahun, @PathVariable int bulan) {
        return CustomResult.any(service.getObject("/statistik/pendidikan2?tahun=" + tahun + "&bulan=" + bulan));
    }

    @GetMapping("/statistik/pendidikan2/excel/{tahun}/{bulan}")
    public ResponseEntity<?> lapStatistikPendidikan2Excel(@PathVariable int tahun, @PathVariable int bulan) {
        String url = "/statistik/pendidikan2/excel?tahun=" + tahun + "&bulan=" + bulan;
        return service.getExport(url);
    }

    @GetMapping("/statistik/umur")
    public ResponseEntity<?> lapStatistikUmur() {
        return CustomResult.any(service.getObject("/statistik/umur"));
    }

    @GetMapping("/statistik/jenis_kelamin")
    public ResponseEntity<?> lapStatistikJenisKelamin() {
        return CustomResult.any(service.getObject("/statistik/jenis_kelamin"));
    }

    @GetMapping("/statistik/gelar_akademik")
    public ResponseEntity<?> lapStatistikGelarAkademik() {
        return CustomResult.any(service.getObject("/statistik/gelar_akademik"));
    }

    @GetMapping("/statistik/agama")
    public ResponseEntity<?> lapStatistikAgama() {
        return CustomResult.any(service.getObject("/statistik/agama"));
    }

    @GetMapping("/statistik/status_pegawai")
    public ResponseEntity<?> lapStatistikStatusPegawai() {
        return CustomResult.any(service.getObject("/statistik/status_pegawai"));
    }

    @GetMapping("/mutasi/{from_date}/{to_date}")
    public ResponseEntity<?> lapMutasi(@PathVariable String from_date, @PathVariable String to_date, @RequestParam(required = false) EJenisMutasi jenis_mutasi) {
        String url = "/mutasi/" + from_date + "/" + to_date;
        if (Objects.nonNull(jenis_mutasi)) url += "?jenis_mutasi=" + jenis_mutasi.name();
        return CustomResult.any(service.getObject(url));
    }

    @GetMapping("/mutasi/excel/{from_date}/{to_date}")
    public ResponseEntity<?> lapMutasiExcel(@PathVariable String from_date, @PathVariable String to_date, @RequestParam(required = false) EJenisMutasi jenis_mutasi) {
        String url = "/mutasi/excel/" + from_date + "/" + to_date;
        if (Objects.nonNull(jenis_mutasi)) url += "?jenis_mutasi=" + jenis_mutasi.name();
        return service.getExport(url);
    }

    @GetMapping("/kontrak")
    public ResponseEntity<?> lapKontrak(@RequestParam(required = false, defaultValue = "AKTIF") EFilterKontrak filter) {
        String url = "/kontrak/";
        if (Objects.nonNull(filter)) url += "?filter=" + filter.name();
        return CustomResult.any(service.getObject(url));
    }

    @GetMapping("/kontrak/excel")
    public ResponseEntity<?> lapKontrakExcel(@RequestParam(required = false, defaultValue = "AKTIF") EFilterKontrak filter) {
        String url = "/kontrak/excel";
        if (Objects.nonNull(filter)) url += "?filter=" + filter.name();
        return service.getExport(url);
    }

    @GetMapping("/lepas_tanggungan_anak")
    public ResponseEntity<?> lapLta(@RequestParam(required = false, defaultValue = "BULAN_INI") EFilterLta filter) {
        String url = "/lepas_tanggungan_anak/";
        if (Objects.nonNull(filter)) url += "?filter=" + filter.name();
        return CustomResult.any(service.getObject(url));
    }

    @GetMapping("/lepas_tanggungan_anak/count")
    public ResponseEntity<?> lapLtaCount(@RequestParam(required = false, defaultValue = "BULAN_INI") EFilterLta filter) {
        String url = "/lepas_tanggungan_anak/count";
        if (Objects.nonNull(filter)) url += "?filter=" + filter.name();
        return CustomResult.any(service.getObject(url));
    }

    @GetMapping("/lepas_tanggungan_anak/excel")
    public ResponseEntity<?> lapLtaExcel(@RequestParam(required = false, defaultValue = "BULAN_INI") EFilterLta filter) {
        String url = "/lepas_tanggungan_anak/excel";
        if (Objects.nonNull(filter)) url += "?filter=" + filter.name();
        return service.getExport(url);
    }

    @GetMapping("/kenaikan_berkala")
    public ResponseEntity<?> lapKenaikanBerkala(@RequestParam(required = false, defaultValue = "BULAN_INI") EFilterKenaikanBerkala filter) {
        String url = "/kenaikan_berkala/";
        if (Objects.nonNull(filter)) url += "?filter=" + filter.name();
        return CustomResult.any(service.getObject(url));
    }

    @GetMapping("/kenaikan_berkala/count")
    public ResponseEntity<?> lapKenaikanBerkalaCount(@RequestParam(required = false, defaultValue = "BULAN_INI") EFilterKenaikanBerkala filter) {
        String url = "/kenaikan_berkala/count";
        if (Objects.nonNull(filter)) url += "?filter=" + filter.name();
        return CustomResult.any(service.getObject(url));
    }

    @GetMapping("/kenaikan_berkala/excel")
    public ResponseEntity<?> lapKenaikanBerkalaExcel(@RequestParam(required = false, defaultValue = "BULAN_INI") EFilterKenaikanBerkala filter) {
        String url = "/kenaikan_berkala/excel";
        if (Objects.nonNull(filter)) url += "?filter=" + filter.name();
        return service.getExport(url);
    }
}
