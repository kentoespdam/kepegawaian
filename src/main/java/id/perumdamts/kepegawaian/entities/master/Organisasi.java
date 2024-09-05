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
import org.hibernate.envers.NotAudited;

@Entity
@Table(indexes = {
        @Index(columnList = "nama"),
        @Index(columnList = "levelOrg"),
        @Index(columnList = "is_deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE organisasi SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted <> 1")
@EqualsAndHashCode(callSuper = true)
@Audited
public class Organisasi extends IdsAbstract {
    @NotAudited
    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Organisasi parent;
    private Integer levelOrg;
    private String nama;

    public Organisasi(Long id) {
        super(id);
    }

    public Organisasi(Long id, Integer levelOrg, String nama) {
        super(id);
        this.levelOrg = levelOrg;
        this.nama = nama;
    }

    public Organisasi(Long id, Organisasi organisasi, Integer levelOrg, String nama) {
        super(id);
        if (organisasi != null)
            this.parent = organisasi;
        this.levelOrg = levelOrg;
        this.nama = nama;
    }
}
