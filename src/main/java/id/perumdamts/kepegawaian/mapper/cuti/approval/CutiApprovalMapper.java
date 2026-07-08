package id.perumdamts.kepegawaian.mapper.cuti.approval;

import id.perumdamts.kepegawaian.dto.cuti.approval.CutiApprovalPostRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiApproval;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;

public final class CutiApprovalMapper {
    private CutiApprovalMapper() {}

    public static CutiApproval toEntity(CutiApprovalPostRequest request, CutiPegawai cutiPegawai, Pegawai approver) {
        CutiApproval entity = new CutiApproval();
        entity.setCutiPegawai(cutiPegawai);
        entity.setApprover(approver);
        entity.setJabatan(approver.getJabatan());
        entity.setNotes(request.getNotes());
        entity.setApprovalLevel(request.getApprovalLevel());
        entity.setApprovalStatus(request.getApprovalStatus());
        return entity;
    }
}
