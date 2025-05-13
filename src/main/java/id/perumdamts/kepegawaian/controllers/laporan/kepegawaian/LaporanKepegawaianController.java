package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.LaporanKepegawaianService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> lapSO(@RequestParam String script_url) {
        return service.getHtml("/so/template?script_url=" + script_url);
    }

    @GetMapping("/statistik/golongan")
    public ResponseEntity<?> lapStatistikGolongan() {
        return CustomResult.any(service.getObject("/statistik/golongan"));
    }

    @GetMapping("/statistik/pendidikan1")
    public ResponseEntity<?> lapStatistikPendidikan1() {
        return CustomResult.any(service.getObject("/statistik/pendidikan1"));
    }

    @GetMapping("/statistik/pendidikan2")
    public ResponseEntity<?> lapStatistikPendidikan2() {
        return CustomResult.any(service.getObject("/statistik/pendidikan2"));
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

}
