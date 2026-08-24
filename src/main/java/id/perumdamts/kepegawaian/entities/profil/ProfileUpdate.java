package id.perumdamts.kepegawaian.entities.profil;

import com.fasterxml.jackson.annotation.JsonFormat;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.history.RevisionMetadata;

import java.time.LocalDateTime;

@Table(name = "ProfilUpdate", indexes = {
        @Index(name = "idx_profile_update_nipam", columnList = "nipam"),
        @Index(name = "idx_profile_update_nama", columnList = "nama"),
        @Index(name = "idx_profile_update", columnList = "approvalStatus"),
        @Index(name = "idx_profile_update_req_date", columnList = "reqDate")
})
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProfileUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nipam;
    private String nama;
    private String jabatan;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime reqDate;
    @Enumerated(EnumType.ORDINAL)
    private EProfileUpdateTable tableName;
    @Enumerated(EnumType.ORDINAL)
    private RevisionMetadata.RevisionType actionType;
    private String dataDescription;
    private String revId;
    @Enumerated(EnumType.ORDINAL)
    private EProfileUpdateApproval approvalStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvalDate;
    private String approvalPic;
}
