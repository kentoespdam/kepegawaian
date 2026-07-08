package id.perumdamts.kepegawaian.dto.cuti.approval;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiMiniResponse;
import id.perumdamts.kepegawaian.mapper.pegawai.pegawai.PegawaiReadMapper;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiApproval;

import java.time.LocalDateTime;

public record CutiApprovalMiniResponse(
        Long id,
        PegawaiMiniResponse approver,
        JabatanMiniResponse jabatan,
        Integer approvalLevel,
        EApprovalCutiStatus approvalStatus,
        String notes,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt
) {
    public static CutiApprovalMiniResponse from(CutiApproval entity) {
        return new CutiApprovalMiniResponse(
                entity.getId(),
                PegawaiReadMapper.toMiniResponse(entity.getApprover()),
                JabatanMiniResponse.from(entity.getJabatan()),
                entity.getApprovalLevel(),
                entity.getApprovalStatus(),
                entity.getNotes(),
                entity.getCreatedAt()
        );
    }
}
