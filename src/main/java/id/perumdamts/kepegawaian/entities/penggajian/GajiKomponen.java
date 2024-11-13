package id.perumdamts.kepegawaian.entities.penggajian;

import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Table(indexes = {
        @Index(columnList = "is_deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE gaji_komponen SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted = false")
@EqualsAndHashCode(callSuper = true)
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class GajiKomponen extends IdsAbstract {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profil_gaji_id", referencedColumnName = "id")
    private GajiProfil profilGaji;
    private String kode;
    private String nama;
    @Enumerated(EnumType.STRING)
    private EJenisGaji jenisGaji;
    private Double nilai;
    // if isReference true, formula will be ignored because data come from another table
    private Boolean isReference = false;
    private String formula;
}
