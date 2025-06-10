package id.perumdamts.kepegawaian.entities.cuti;

import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;

@Entity
@Table(indexes = {
        @Index(name = "is_deleted_idx", columnList = "is_deleted")
})
@Data
@Audited
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE cuti_pegawai SET is_deleted = true where id = ?")
@SQLRestriction("WHERE is_deleted = false")
@NoArgsConstructor
@AllArgsConstructor
public class CutiApproval extends IdsAbstract {
    @ManyToOne
    @JoinColumn(name = "cuti_pegawai_id", referencedColumnName = "id")
    private CutiPegawai cutiPegawai;
    @ManyToOne
    @JoinColumn(name = "approver_id", referencedColumnName = "id")
    private Pegawai approver;
    @ManyToOne
    @JoinColumn(name = "jabatan_id", referencedColumnName = "id")
    private Jabatan jabatan;
    private Integer approvalLevel;
    @Enumerated(EnumType.ORDINAL)
    private EApprovalCutiStatus approvalStatus;
    @Column(columnDefinition = "TEXT")
    private String notes;
}
