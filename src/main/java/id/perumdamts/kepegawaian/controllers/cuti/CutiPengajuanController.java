package id.perumdamts.kepegawaian.controllers.cuti;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.cuti.approvalChain.CutiApprovalChainRequest;
import id.perumdamts.kepegawaian.dto.cuti.approvalChain.CutiApprovalChainResponse;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.*;
import id.perumdamts.kepegawaian.exceptions.BadRequestException;
import id.perumdamts.kepegawaian.services.cuti.CutiOwnershipService;
import id.perumdamts.kepegawaian.services.cuti.approvalChain.CutiInboxQueryService;
import id.perumdamts.kepegawaian.services.cuti.klaim.KlaimCutiCommand;
import id.perumdamts.kepegawaian.services.cuti.pengajuan.CutiPengajuanQueryService;
import id.perumdamts.kepegawaian.services.cuti.pengajuan.PengajuanCutiCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Objects;

@RestController
@RequestMapping("/cuti/pengajuan")
@RequiredArgsConstructor
public class CutiPengajuanController {
    private final CutiPengajuanQueryService queryService;
    private final CutiInboxQueryService cutiInboxQueryService;
    private final PengajuanCutiCommand pengajuanCutiCommand;
    private final KlaimCutiCommand klaimCutiCommand;
    private final CutiOwnershipService ownershipService;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CUTI:READ')")
    @GetMapping
    public ResponseEntity<PageResult<Page<CutiPengajuanResponse>>> index(@Valid @ParameterObject CutiPengajuanRequest request) {
        return CustomResult.page(queryService.findPage(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CUTI:APPROVE')")
    @GetMapping("/approval")
    public ResponseEntity<PageResult<Page<CutiApprovalChainResponse>>> indexApproval(@Valid @ParameterObject CutiApprovalChainRequest request) {
        return CustomResult.page(cutiInboxQueryService.findCutiPegawai(request));
    }

    // Self-service: non-ADMIN/HRD hanya bisa melihat daftar cuti milik sendiri (ADR-0038)
    @GetMapping("/{pegawaiId}/pegawai")
    public ResponseEntity<PageResult<Page<CutiPengajuanResponse>>> index(@PathVariable Long pegawaiId, @Valid @ParameterObject CutiPengajuanRequest request) {
        request.setPegawaiId(ownershipService.resolvePemohon(pegawaiId).getId());
        return CustomResult.page(queryService.findPage(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<CutiPengajuanResponse>> detail(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id));
    }

    // Kalkulator jumlah hari kerja — murni perhitungan, tanpa data — login-only
    @GetMapping("/{tanggalMulai}/{tanggalSelesai}/total-hari-kerja")
    public ResponseEntity<SingleResult<Integer>> findTotalHariKerja(@PathVariable LocalDate tanggalMulai, @PathVariable LocalDate tanggalSelesai) {
        if (Objects.isNull(tanggalMulai) || Objects.isNull(tanggalSelesai))
            throw new BadRequestException("Tanggal mulai dan selesai harus di isi");
        if (tanggalSelesai.isBefore(tanggalMulai))
            throw new BadRequestException("Tanggal selesai tidak boleh dibuat sebelum tanggal mulai");
        return CustomResult.any(queryService.findTotalHariKerja(tanggalMulai, tanggalSelesai));
    }

    @PostMapping
    public ResponseEntity<SavedResult<Long>> create(@Valid @RequestBody CutiPengajuanPostRequest request) {
        if (request.getTanggalMulai().isAfter(request.getTanggalSelesai()))
            throw new BadRequestException("Tanggal selesai tidak boleh dibuat sebelum tanggal mulai");
        if (request.getTanggalMulai().isBefore(LocalDate.now()))
            throw new BadRequestException("Pengajuan cuti tidak boleh dibuat sebelum tanggal sekarang");
        return CustomResult.save(pengajuanCutiCommand.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody CutiPengajuanPutRequest request) {
        if (request.getTanggalMulai().isAfter(request.getTanggalSelesai()))
            throw new BadRequestException("Tanggal selesai tidak boleh dibuat sebelum tanggal mulai");
        if (request.getTanggalMulai().isBefore(LocalDate.now()))
            throw new BadRequestException("Pengajuan cuti tidak boleh dibuat sebelum tanggal sekarang");
        return CustomResult.save(pengajuanCutiCommand.update(id, request));
    }

    @PostMapping("/klaim")
    public ResponseEntity<SavedResult<Long>> klaim(@Valid @RequestBody CutiPengajuanKlaimPostRequest request) {
        return CustomResult.save(klaimCutiCommand.save(request));
    }

    @PutMapping("/klaim/{id}")
    public ResponseEntity<SavedResult<Long>> updateKlaim(@PathVariable Long id, @Valid @RequestBody CutiPengajuanKlaimPostRequest request) {
        return CustomResult.save(klaimCutiCommand.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> pembatalan(@PathVariable Long id) {
        return CustomResult.delete(pengajuanCutiCommand.pembatalan(id));
    }
}
