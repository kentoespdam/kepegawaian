package id.perumdamts.kepegawaian.dto.profil.profileUpdate;

import com.fasterxml.jackson.annotation.JsonFormat;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import lombok.Data;
import org.springframework.data.history.RevisionMetadata;

import java.time.LocalDateTime;

@Data
public class ProfileUpdateQuery {
    private Long id;
    private String nipam;
    private String nama;
    private String jabatan;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reqDate;
    private EProfileUpdateTable tableName;
    private RevisionMetadata.RevisionType actionType;
    private String dataDescription;
    private Long revId;
    private EProfileUpdateApproval approvalStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvalDate;
    private String approvalPic;
}
