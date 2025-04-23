package id.perumdamts.kepegawaian.entities.cuti;

import com.fasterxml.jackson.annotation.JsonBackReference;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;

@Entity
@Table(indexes = {
    @Index(name = "is_deleted_idx", columnList = "is_deleted")
})
@Data
@Audited
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE kuota_cuti SET is_deleted = true where id = ?")
@SQLRestriction("WHERE is_deleted = false")
@NoArgsConstructor
@AllArgsConstructor
public class KuotaCuti extends IdsAbstract {
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "pegawai_id", referencedColumnName = "id")
    private Pegawai pegawai;
    private Integer tahun;
    private Integer kuota;
    private Integer terpakai;
    private Integer sisa;
}
