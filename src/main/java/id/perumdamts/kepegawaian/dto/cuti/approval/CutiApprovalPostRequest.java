package id.perumdamts.kepegawaian.dto.cuti.approval;

import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CutiApprovalPostRequest {
    @NotNull(message = "CSRF token is required")
    @NotBlank(message = "CSRF token is required")
    private String csrfToken;
    @NotNull(message = "Cuti is required")
    @Min(value = 1, message = "Cuti is required")
    private Long cutiId;
    @NotNull(message = "Approver is required")
    @Min(value = 1, message = "Approver is required")
    private Long approverId;
    @NotNull(message = "Approval level is required")
    @Min(value = 1, message = "Approval level is required")
    private Integer approvalLevel;
    @NotNull(message = "Approval status is required")
    @Enumerated(EnumType.ORDINAL)
    private EApprovalCutiStatus approvalStatus;
    private String notes;
}
