package id.perumdamts.kepegawaian.dto.cuti.approval;

import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiMiniResponse;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiApproval;
import lombok.Data;

@Data
public class CutiApprovalMiniResponse {
    private Long id;
    private PegawaiMiniResponse approver;
    private JabatanMiniResponse jabatan;
    private Integer approvalLevel;
    private EApprovalCutiStatus approvalStatus;
    private String notes;

    public static CutiApprovalMiniResponse from(CutiApproval entity) {
        CutiApprovalMiniResponse response = new CutiApprovalMiniResponse();
        response.setId(entity.getId());
        response.setApprover(PegawaiMiniResponse.from(entity.getApprover()));
        response.setJabatan(JabatanMiniResponse.from(entity.getJabatan()));
        response.setApprovalLevel(entity.getApprovalLevel());
        response.setApprovalStatus(entity.getApprovalStatus());
        response.setNotes(entity.getNotes());
        return response;
    }
}
