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
        @Index(columnList = "is_deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE jabatan SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted <> 1")
@EqualsAndHashCode(callSuper = true)
@Audited
public class Jabatan extends IdsAbstract {
    @NotAudited
    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Jabatan parent;
    @NotAudited
    @ManyToOne
    @JoinColumn(name = "organisasi_id", referencedColumnName = "id")
    private Organisasi organisasi;
    @NotAudited
    @ManyToOne
    @JoinColumn(name = "level_id", referencedColumnName = "id")
    private Level level;
    private String nama;
    private Boolean isDeleted = false;

    public Jabatan(Long id) {
        super(id);
    }

    public Jabatan(long id, Jabatan parent, Organisasi organisasi, Level level, String direkturUtama) {
        super(id);
        if (parent != null)
            this.parent = parent;
        this.organisasi = organisasi;
        this.level = level;
        this.nama = direkturUtama;
    }
}
