package id.perumdamts.kepegawaian.entities.penggajian;


import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(indexes = {
        @Index(columnList = "kode"),
        @Index(columnList = "is_deleted")
})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE gaji_pendapatan_non_pajak SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted = FALSE")
@EntityListeners(AuditingEntityListener.class)
@Audited
public class GajiPendapatanNonPajak extends IdsAbstract {
    private String kode;
    private Double nominal;
    private String notes;

    public GajiPendapatanNonPajak(Long id, String kode, Double nominal, String notes) {
        super(id);
        this.kode = kode;
        this.nominal = nominal;
        this.notes = notes;
    }
}
