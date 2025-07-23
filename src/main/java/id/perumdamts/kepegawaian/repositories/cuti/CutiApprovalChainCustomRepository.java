package id.perumdamts.kepegawaian.repositories.cuti;

import id.perumdamts.kepegawaian.dto.cuti.approvalChain.CutiApprovalChainResponse;
import id.perumdamts.kepegawaian.dto.cuti.approvalChain.CutiApprovalChainRequest;
import org.springframework.data.domain.Page;

public interface CutiApprovalChainCustomRepository {
    Page<CutiApprovalChainResponse> findPage(CutiApprovalChainRequest request);
}
