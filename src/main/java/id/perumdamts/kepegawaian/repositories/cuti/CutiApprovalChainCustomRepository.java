package id.perumdamts.kepegawaian.repositories.cuti;

import id.perumdamts.kepegawaian.dto.cuti.approvalChain.CutiApprovalChainRequest;
import id.perumdamts.kepegawaian.dto.cuti.approvalChain.CutiApprovalChainResponse;
import org.springframework.data.domain.Page;

public interface CutiApprovalChainCustomRepository {
    Page<CutiApprovalChainResponse> findPageApproval(CutiApprovalChainRequest request);
}
