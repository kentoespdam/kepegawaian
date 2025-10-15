package id.perumdamts.kepegawaian.entities.penggajian;


import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Table(indexes = {
        @Index(columnList = "is_deleted")
})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE gaji_profil SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted = FALSE")

@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class GajiProfil extends IdsAbstract {
    private String nama;

    public GajiProfil(Long id, String nama) {
        super(id);
        this.nama = nama;
    }

    public GajiProfil(Long id) {
        super(id);
    }
}
