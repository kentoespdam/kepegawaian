package id.perumdamts.kepegawaian.dto.cuti.approval;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CutiApprovalRequest extends PagedRequest {
    private Long id;
    private Long cutiId;
    private Long approverId;
    private Long jabatanId;
}
