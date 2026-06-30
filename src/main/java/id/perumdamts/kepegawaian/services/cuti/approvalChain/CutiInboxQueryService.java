package id.perumdamts.kepegawaian.services.cuti.approvalChain;

import id.perumdamts.kepegawaian.dto.cuti.approvalChain.CutiApprovalChainRequest;
import id.perumdamts.kepegawaian.dto.cuti.approvalChain.CutiApprovalChainResponse;
import id.perumdamts.kepegawaian.repositories.cuti.jooq.CutiInboxQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CutiInboxQueryService {
    private final CutiInboxQueryRepository inboxQueryRepository;

    public Page<CutiApprovalChainResponse> findCutiPegawai(CutiApprovalChainRequest request) {
        return inboxQueryRepository.pageQuery(request);
    }
}
