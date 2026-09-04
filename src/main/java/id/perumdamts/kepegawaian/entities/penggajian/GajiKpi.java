package id.perumdamts.kepegawaian.entities.penggajian;

import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(indexes = {
        @Index(columnList = "nipam"),
        @Index(columnList = "periode"),
        @Index(columnList = "is_deleted")
}, uniqueConstraints = @UniqueConstraint(name = "uk_gj_kpi_nipam_periode", columnNames = {"nipam", "periode"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE gaji_kpi SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted = FALSE")
@EntityListeners(AuditingEntityListener.class)
@Audited
public class GajiKpi extends IdsAbstract {
    private String nipam;
    @Column(length = 7)
    private String periode;
    private Double tunkin;
    @Column(name = "pph21_ter")
    private Double pph21Ter;
}
