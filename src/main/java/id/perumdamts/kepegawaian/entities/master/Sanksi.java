package id.perumdamts.kepegawaian.entities.master;

import id.perumdamts.kepegawaian.entities.commons.MasterBaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;


@Entity
@Table(name = "sanksi_sp", indexes = {
        @Index(columnList = "kode", unique = true),
        @Index(columnList = "is_deleted")
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE sanksi_sp SET is_deleted = TRUE WHERE id = ?")
public class Sanksi extends MasterBaseEntity {
    private String kode;
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

    public Sanksi(String kode, String keterangan, JenisSp jenisSp, Boolean potTkk, Integer jmlPotTkk, Boolean isPendingPangkat, Boolean isPendingGaji, Boolean isTurunPangkat, Boolean isTurunJabatan, Boolean isSuspension, Boolean isTerminateDh, Boolean isTerminateTh) {
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
