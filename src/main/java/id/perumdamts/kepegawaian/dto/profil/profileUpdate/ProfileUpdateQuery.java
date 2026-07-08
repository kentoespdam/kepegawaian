package id.perumdamts.kepegawaian.dto.profil.profileUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
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
) {}
