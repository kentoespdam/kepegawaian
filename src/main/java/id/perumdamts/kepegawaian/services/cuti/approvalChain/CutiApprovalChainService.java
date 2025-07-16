package id.perumdamts.kepegawaian.services.cuti.approvalChain;

import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;

public interface CutiApprovalChainService {
    void generateApprovalChain(CutiPegawai cutiPegawai);
}
