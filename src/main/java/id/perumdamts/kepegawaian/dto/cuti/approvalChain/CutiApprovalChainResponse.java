package id.perumdamts.kepegawaian.dto.cuti.approvalChain;

import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanResponse;
import id.perumdamts.kepegawaian.entities.commons.EReadWriteStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CutiApprovalChainResponse {
    private Long id;
    private Integer approvalLevel;
    @Enumerated(EnumType.ORDINAL)
    private EReadWriteStatus readWriteStatus;
    private CutiPengajuanResponse refCuti;

    public static CutiApprovalChainResponse from(CutiApprovalChain entity) {
        CutiApprovalChainResponse response = new CutiApprovalChainResponse();
        response.setId(entity.getId());
        response.setReadWriteStatus(entity.getReadWriteStatus());
        response.setRefCuti(CutiPengajuanResponse.from(entity.getRefCuti()));
        response.setApprovalLevel(entity.getApprovalLevel());
        return response;
    }
}
