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
@SQLRestriction("is_deleted = false")
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

    @Override
    public String toString() {
        return "CutiApproval{" +
                "cutiPegawai=" + cutiPegawai +
                ", approver= Pegawai( " +
                "id=" + approver.getId() +
                ", nipam='" + approver.getNipam() + '\'' +
                ", nama='" + approver.getBiodata().getNama() + '\'' +
                ", jabatan= Jabatan( id=" + approver.getJabatan().getId() +
                ", nama='" + approver.getJabatan().getNama() + '\'' +
                ")) " +
                ", jabatan=" + jabatan +
                ", approvalLevel=" + approvalLevel +
                ", approvalStatus=" + approvalStatus +
                ", notes='" + notes + '\'' +
                '}';
    }
}
