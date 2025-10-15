package id.perumdamts.kepegawaian.entities.master;

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
        @Index(columnList = "nama"),
        @Index(columnList = "is_deleted")
})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE rumah_dinas SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted = FALSE")

@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class RumahDinas extends IdsAbstract {
    private String nama;
    private Double nilai;

    public RumahDinas(Long id, String nama, double nilai) {
        super(id);
        this.nama = nama;
        this.nilai = nilai;
    }
}
