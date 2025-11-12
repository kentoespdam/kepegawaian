package id.perumdamts.kepegawaian.entities.profil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;


@Entity
@Table(indexes = {
        @Index(columnList = "jenjang_id"),
        @Index(columnList = "is_deleted"),
        @Index(columnList = "is_latest"),
        @Index(columnList = "disetujuiOleh")
}, uniqueConstraints = {
        @UniqueConstraint(columnNames = {"biodata_id", "jenjang_id", "tahun_masuk"})
})
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE pendidikan SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class Pendidikan extends IdsAbstract {
    @ManyToOne
    @JoinColumn(name = "biodata_id", referencedColumnName = "nik")
    @JsonIgnore
    private Biodata biodata;
    @ManyToOne
    @JoinColumn(name = "jenjang_id", referencedColumnName = "id")
    private JenjangPendidikan jenjangPendidikan;
    private String gelarDepan;
    private String gelarBelakang;
    private String jurusan;
    private String institusi;
    private String kota;
    private Integer tahunMasuk;
    private Boolean isLulus;
    private Integer tahunLulus;
    private Double gpa;
    @Column(columnDefinition = "boolean default false")
    private Boolean isLatest;
    private Boolean changedStatus;
}
