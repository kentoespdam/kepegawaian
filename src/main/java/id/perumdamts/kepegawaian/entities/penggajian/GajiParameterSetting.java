package id.perumdamts.kepegawaian.entities.penggajian;


import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;

@Entity
@Table(indexes = {
        @Index(columnList = "is_deleted")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE gaji_parameter_setting SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted = FALSE")

@Audited
public class GajiParameterSetting extends IdsAbstract {
    private String kode;
    private Double nominal;

    public GajiParameterSetting(Long id, String kode, Double nominal) {
        super(id);
        this.kode = kode;
        this.nominal = nominal;
    }
}