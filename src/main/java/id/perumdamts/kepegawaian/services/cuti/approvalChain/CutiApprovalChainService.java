package id.perumdamts.kepegawaian.services.cuti.approvalChain;

import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanApprovalRequest;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanResponse;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import org.springframework.data.domain.Page;

public interface CutiApprovalChainService {
    Page<CutiPengajuanResponse> findCutiPegawai(CutiPengajuanApprovalRequest request);
    void generateApprovalChain(CutiPegawai cutiPegawai);
}
