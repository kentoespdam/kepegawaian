package id.perumdamts.kepegawaian.dto.cuti.approvalChain;

import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EReadWriteStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;

public record CutiApprovalChainResponse(
        Long id,
        Integer approvalLevel,
        EReadWriteStatus readWriteStatus,
        CutiPengajuanMiniResponse refCuti
) {
    public static CutiApprovalChainResponse from(CutiApprovalChain entity) {
        return new CutiApprovalChainResponse(
                entity.getId(),
                entity.getApprovalLevel(),
                entity.getReadWriteStatus(),
                CutiPengajuanMiniResponse.from(entity.getRefCuti())
        );
    }
}
