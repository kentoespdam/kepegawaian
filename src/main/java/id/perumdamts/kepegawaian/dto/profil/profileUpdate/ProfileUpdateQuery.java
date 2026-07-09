package id.perumdamts.kepegawaian.dto.profil.profileUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import org.springframework.data.history.RevisionMetadata;

import java.time.LocalDateTime;

public record ProfileUpdateQuery(
        Long id,
        String nipam,
        String nama,
        String jabatan,
        LocalDateTime reqDate,
        EProfileUpdateTable tableName,
        RevisionMetadata.RevisionType actionType,
        String dataDescription,
        Long revId,
        EProfileUpdateApproval approvalStatus,
        LocalDateTime approvalDate,
        String approvalPic
) {
    public static ProfileUpdateQuery from(ProfileUpdate entity) {
        if (entity == null) return null;
        return new ProfileUpdateQuery(
                entity.getId(),
                entity.getNipam(),
                entity.getNama(),
                entity.getJabatan(),
                entity.getReqDate(),
                entity.getTableName(),
                entity.getActionType(),
                entity.getDataDescription(),
                entity.getRevId(),
                entity.getApprovalStatus(),
                entity.getApprovalDate(),
                entity.getApprovalPic()
        );
    }
}
