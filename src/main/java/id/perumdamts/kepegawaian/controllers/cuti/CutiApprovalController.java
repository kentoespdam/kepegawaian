package id.perumdamts.kepegawaian.controllers.cuti;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalRequest;
import id.perumdamts.kepegawaian.services.cuti.approval.CutiApprovalQueryService;
import id.perumdamts.kepegawaian.services.cuti.approval.ApprovalCutiCommand;
import id.perumdamts.kepegawaian.services.cuti.klaim.KlaimCutiCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cuti/approval")
@RequiredArgsConstructor
public class CutiApprovalController {
    private final CutiApprovalQueryService queryService;
    private final ApprovalCutiCommand approvalCutiCommand;
    private final KlaimCutiCommand klaimCutiCommand;

    @GetMapping("/{cutiId}")
    public ResponseEntity<?> findApproval(@PathVariable Long cutiId, @ParameterObject CutiApprovalRequest request) {
        request.setCutiId(cutiId);
        return CustomResult.page(queryService.findPage(cutiId, request));
    }

    @PostMapping
    public ResponseEntity<?> saveApproval(@Valid @RequestBody CutiApprovalPostRequest request, Errors errors) {
        return CustomResult.save(approvalCutiCommand.savePengajuan(request));
    }

    @PostMapping("/klaim")
    public ResponseEntity<?> klaim(@Valid @RequestBody CutiApprovalPostRequest request, Errors errors) {
        return CustomResult.save(klaimCutiCommand.saveKlaim(request));
    }
}
