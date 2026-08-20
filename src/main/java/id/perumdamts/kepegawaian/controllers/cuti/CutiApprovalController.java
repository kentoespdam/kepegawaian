package id.perumdamts.kepegawaian.controllers.cuti;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.PageResult;
import id.perumdamts.kepegawaian.dto.commons.SavedResult;
import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalMiniResponse;
import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalRequest;
import id.perumdamts.kepegawaian.services.cuti.approval.ApprovalCutiCommand;
import id.perumdamts.kepegawaian.services.cuti.approval.CutiApprovalQueryService;
import id.perumdamts.kepegawaian.services.cuti.klaim.KlaimCutiCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Cuti — Cuti Approval")
@RestController
@RequestMapping("/cuti/approval")
@RequiredArgsConstructor
public class CutiApprovalController {
    private final CutiApprovalQueryService queryService;
    private final ApprovalCutiCommand approvalCutiCommand;
    private final KlaimCutiCommand klaimCutiCommand;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CUTI:APPROVE')")
    @Operation(summary = "find approval")
    @GetMapping("/{cutiId}")
    public ResponseEntity<PageResult<Page<CutiApprovalMiniResponse>>> findApproval(@PathVariable Long cutiId, @Valid @ParameterObject CutiApprovalRequest request) {
        request.setCutiId(cutiId);
        return CustomResult.page(queryService.findPage(cutiId, request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CUTI:APPROVE')")
    @Operation(summary = "save approval")
    @PostMapping
    public ResponseEntity<SavedResult<String>> saveApproval(@Valid @RequestBody CutiApprovalPostRequest request) {
        return CustomResult.save(approvalCutiCommand.savePengajuan(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CUTI:APPROVE')")
    @Operation(summary = "klaim")
    @PostMapping("/klaim")
    public ResponseEntity<SavedResult<String>> klaim(@Valid @RequestBody CutiApprovalPostRequest request) {
        return CustomResult.save(klaimCutiCommand.saveKlaim(request));
    }
}
