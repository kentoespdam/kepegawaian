package id.perumdamts.kepegawaian.services.cuti.approvalChain;

import id.perumdamts.kepegawaian.dto.cuti.approvalChain.CutiApprovalChainRequest;
import id.perumdamts.kepegawaian.dto.cuti.approvalChain.CutiApprovalChainResponse;
import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CutiApprovalChainService {
    Page<CutiApprovalChainResponse> findCutiPegawai(CutiApprovalChainRequest request);
    List<CutiApprovalChain> generateApprovalChain(CutiPegawai cutiPegawai);
    void generateApprovalKlaimChain(CutiPegawai cutiPegawai);
}
