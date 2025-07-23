package id.perumdamts.kepegawaian.controllers.cuti;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalRequest;
import id.perumdamts.kepegawaian.services.cuti.approval.CutiApprovalService;
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
    private final CutiApprovalService service;

    @GetMapping("/{cutiId}")
    public ResponseEntity<?> findApproval(@PathVariable Long cutiId, @ParameterObject CutiApprovalRequest request) {
        request.setCutiId(cutiId);
        return CustomResult.page(service.findPage(cutiId, request));
    }

    @PostMapping
    public ResponseEntity<?> saveApproval(@Valid @RequestBody CutiApprovalPostRequest request, Errors errors) {
        return CustomResult.save(service.savePengajuan(request));
    }
}
