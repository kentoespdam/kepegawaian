package id.perumdamts.kepegawaian.controllers.cuti;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanRequest;
import id.perumdamts.kepegawaian.services.cuti.pengajuan.CutiPengajuanPostRequest;
import id.perumdamts.kepegawaian.services.cuti.pengajuan.CutiPengajuanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Objects;

@RestController
@RequestMapping("/cuti/pengajuan")
@RequiredArgsConstructor
public class CutiPengajuanController {
    private final CutiPengajuanService service;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject CutiPengajuanRequest request) {
        return CustomResult.page(service.findPage(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(Long id) {
        return CustomResult.any(service.findById(id));
    }

    @GetMapping("/{tanggalMulai}/{tanggalSelesai}/total-hari-kerja")
    public ResponseEntity<?> findTotalHariKerja(@PathVariable LocalDate tanggalMulai, @PathVariable LocalDate tanggalSelesai) {
        if (Objects.isNull(tanggalMulai) || Objects.isNull(tanggalSelesai))
            return ErrorResult.build("Tanggal mulai dan selesai harus diisi");
        if (tanggalMulai.isBefore(tanggalSelesai))
            return ErrorResult.build("Tanggal selesai tidak boleh dibuat sebelum tanggal mulai");
        return CustomResult.any(service.findTotalHariKerja(tanggalMulai, tanggalSelesai));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CutiPengajuanPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        if (request.getTanggalMulai().isAfter(request.getTanggalSelesai()))
            return ErrorResult.build("Tanggal selesai tidak boleh dibuat sebelum tanggal mulai");
//        if (request.getTanggalMulai().isBefore(LocalDate.now()))
//            return ErrorResult.build("Pengajuan cuti tidak boleh dibuat sebelum tanggal sekarang");
        return CustomResult.save(service.save(request));
    }

    @PatchMapping("/{id}/pembatalan")
    public ResponseEntity<?> pembatalan(@PathVariable Long id) {
        return CustomResult.save(service.pembatalan(id));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return CustomResult.delete(service.delete(id));
    }
}
