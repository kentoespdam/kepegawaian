package id.perumdamts.kepegawaian.entities.kepegawaian;


import com.fasterxml.jackson.annotation.JsonBackReference;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(indexes = {
        @Index(columnList = "is_deleted"),

})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE riwayat_cuti SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted <> 1")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class RiwayatCuti extends IdsAbstract {
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "pegawai_id", referencedColumnName = "id")
    private Pegawai pegawai;
    private String nipam;
    private String nama;

}
