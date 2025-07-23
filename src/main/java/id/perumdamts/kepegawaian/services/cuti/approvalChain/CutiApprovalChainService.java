package id.perumdamts.kepegawaian.services.cuti.approvalChain;

import id.perumdamts.kepegawaian.dto.cuti.approvalChain.CutiApprovalChainResponse;
import id.perumdamts.kepegawaian.dto.cuti.approvalChain.CutiApprovalChainRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import org.springframework.data.domain.Page;

public interface CutiApprovalChainService {
    Page<CutiApprovalChainResponse> findCutiPegawai(CutiApprovalChainRequest request);
    void generateApprovalChain(CutiPegawai cutiPegawai);
}
