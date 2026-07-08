package id.perumdamts.kepegawaian.dto.cuti.approvalChain;

import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanResponse;
import id.perumdamts.kepegawaian.entities.commons.EReadWriteStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;

public record CutiApprovalChainResponse(
        Long id,
        Integer approvalLevel,
        EReadWriteStatus readWriteStatus,
        CutiPengajuanResponse refCuti
) {
    public static CutiApprovalChainResponse from(CutiApprovalChain entity) {
        return new CutiApprovalChainResponse(
                entity.getId(),
                entity.getApprovalLevel(),
                entity.getReadWriteStatus(),
                CutiPengajuanResponse.from(entity.getRefCuti())
        );
    }
}
