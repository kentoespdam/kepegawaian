package id.perumdamts.kepegawaian.entities.penggajian;

import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;

@Entity
@Table(indexes = {
        @Index(columnList = "is_deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE gaji_phdp SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted = false")
@EqualsAndHashCode(callSuper = true)
@Audited
public class GajiPhdp extends IdsAbstract {
    @Column(unique = true)
    private Integer urut;
    private String kondisi;
    private String formula;
}
