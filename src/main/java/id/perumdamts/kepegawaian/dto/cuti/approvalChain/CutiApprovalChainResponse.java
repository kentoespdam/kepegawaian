package id.perumdamts.kepegawaian.dto.cuti.approvalChain;

import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanResponse;
import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;
import lombok.Data;

@Data
public class CutiApprovalChainResponse {
    private Long id;
    private Boolean skip;
    private CutiPengajuanResponse refCuti;

    public static CutiApprovalChainResponse from(CutiApprovalChain entity) {
        CutiApprovalChainResponse response = new CutiApprovalChainResponse();
        response.setId(entity.getId());
        response.setSkip(entity.getSkip());
        response.setRefCuti(CutiPengajuanResponse.from(entity.getRefCuti()));
        return response;
    }
}
