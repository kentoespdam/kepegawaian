package id.perumdamts.kepegawaian.entities.cuti;

import com.fasterxml.jackson.annotation.JsonBackReference;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
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
@SQLDelete(sql = "UPDATE cuti_jenis SET is_deleted = true where id = ?")
@SQLRestriction("is_deleted = false")
@NoArgsConstructor
@AllArgsConstructor
public class CutiJenis extends IdsAbstract {
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @NotFound(action = NotFoundAction.IGNORE)
    private CutiJenis parent;
    private String nama;
    private Integer maxHari;
    private Boolean potongKuotaTahunan = false;

    public CutiJenis(Long id) {
        super(id);
    }

    public CutiJenis(Long id, CutiJenis parent, String nama, Integer maxHari, Boolean potongKuotaTahunan) {
        super(id);
        if (parent != null)
            this.parent = parent;
        this.nama = nama;
        this.maxHari = maxHari;
        this.potongKuotaTahunan = potongKuotaTahunan;
    }
}
