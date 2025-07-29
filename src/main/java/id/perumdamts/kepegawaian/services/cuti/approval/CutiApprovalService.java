package id.perumdamts.kepegawaian.services.cuti.approval;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalMiniResponse;
import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalRequest;
import org.springframework.data.domain.Page;

public interface CutiApprovalService {
    Page<CutiApprovalMiniResponse> findPage(Long cutiId, CutiApprovalRequest request);

    SavedStatus<?> savePengajuan(CutiApprovalPostRequest request);

    SavedStatus<?> saveKlaim(CutiApprovalPostRequest request);
}
