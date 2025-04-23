package id.perumdamts.kepegawaian.entities.master;

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

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sanksi_sp", indexes = {
        @Index(columnList = "kode", unique = true),
        @Index(columnList = "is_deleted")
})
@NoArgsConstructor
@AllArgsConstructor
@Data
@SQLDelete(sql = "UPDATE sanksi_sp SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class Sanksi extends IdsAbstract {
    @Column(name = "kode", nullable = false, columnDefinition = "VARCHAR(10)")
    private String kode;
    @Column(columnDefinition = "TEXT")
    private String keterangan;
    private Boolean potTkk = false;
    private Integer jmlPotTkk = 0;
    private Boolean isPendingPangkat = false;
    private Boolean isPendingGaji = false;
    private Boolean isTurunPangkat = false;
    private Boolean isTurunJabatan = false;
    private Boolean isSuspension = false;
    private Boolean isTerminateDh = false;
    private Boolean isTerminateTh = false;

    @ManyToOne
    @JoinColumn(name = "jenis_sp_id", referencedColumnName = "id")
    private JenisSp jenisSp;

    public Sanksi(String kode, String keterangan) {
        this.kode = kode;
        this.keterangan = keterangan;
    }

    public Sanksi(Long id, String kode, String keterangan, JenisSp jenisSp, Boolean potTkk, Integer jmlPotTkk, Boolean isPendingPangkat, Boolean isPendingGaji, Boolean isTurunPangkat, Boolean isTurunJabatan, Boolean isSuspension, Boolean isTerminateDh, Boolean isTerminateTh) {
        super(id);
        this.kode = kode;
        this.keterangan = keterangan;
        this.jenisSp = jenisSp;
        this.potTkk = potTkk;
        this.jmlPotTkk = jmlPotTkk;
        this.isPendingPangkat = isPendingPangkat;
        this.isPendingGaji = isPendingGaji;
        this.isTurunPangkat = isTurunPangkat;
        this.isTurunJabatan = isTurunJabatan;
        this.isSuspension = isSuspension;
        this.isTerminateDh = isTerminateDh;
        this.isTerminateTh = isTerminateTh;
    }
}
