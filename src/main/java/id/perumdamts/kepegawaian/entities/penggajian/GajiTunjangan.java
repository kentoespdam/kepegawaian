package id.perumdamts.kepegawaian.entities.penggajian;

import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Level;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@NoArgsConstructor
@SQLDelete(sql = "UPDATE gaji_tunjangan SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted = FALSE")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class GajiTunjangan extends IdsAbstract {
    @Enumerated(EnumType.ORDINAL)
    private EJenisTunjangan jenisTunjangan;
    @ManyToOne
    @JoinColumn(name = "level_id", referencedColumnName = "id")
    private Level level;
    @ManyToOne
    @JoinColumn(name = "golongan_id", referencedColumnName = "id")
    private Golongan golongan;
    private Double nominal;

    public GajiTunjangan(EJenisTunjangan jenisTunjangan, Level level, Golongan golongan, Double nominal) {
        this.jenisTunjangan = jenisTunjangan;
        this.level = level;
        if (golongan != null)
            this.golongan = golongan;
        this.nominal = nominal;
    }

    public GajiTunjangan(EJenisTunjangan jenisTunjangan, Level level, Double nominal) {
        this.jenisTunjangan = jenisTunjangan;
        this.level = level;
        this.nominal = nominal;
    }
}
