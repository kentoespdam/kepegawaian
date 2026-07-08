package id.perumdamts.kepegawaian.dto.cuti.approval;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiMiniResponse;
import id.perumdamts.kepegawaian.mapper.pegawai.pegawai.PegawaiReadMapper;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiApproval;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CutiApprovalMiniResponse {
    private Long id;
    private PegawaiMiniResponse approver;
    private JabatanMiniResponse jabatan;
    private Integer approvalLevel;
    private EApprovalCutiStatus approvalStatus;
    private String notes;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static CutiApprovalMiniResponse from(CutiApproval entity) {
        CutiApprovalMiniResponse response = new CutiApprovalMiniResponse();
        response.setId(entity.getId());
        response.setApprover(PegawaiReadMapper.toMiniResponse(entity.getApprover()));
        response.setJabatan(JabatanMiniResponse.from(entity.getJabatan()));
        response.setApprovalLevel(entity.getApprovalLevel());
        response.setApprovalStatus(entity.getApprovalStatus());
        response.setNotes(entity.getNotes());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }
}
