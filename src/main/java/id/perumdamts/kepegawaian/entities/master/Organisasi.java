package id.perumdamts.kepegawaian.entities.master;

import com.fasterxml.jackson.annotation.JsonBackReference;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Table(indexes = {
        @Index(columnList = "kode"),
        @Index(columnList = "nama"),
        @Index(columnList = "levelOrg"),
        @Index(columnList = "is_deleted")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE organisasi SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted <> 1")

@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class Organisasi extends IdsAbstract {
    private String kode;
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @NotFound(action = NotFoundAction.IGNORE)
    private Organisasi parent;
    private Integer levelOrg;
    private String nama;
    private String shortName;
    private String category;

    public Organisasi(Long id) {
        super(id);
    }

    public Organisasi(Long id, String kode, Organisasi organisasi, Integer levelOrg, String nama, String shortName) {
        super(id);
        this.kode = kode;
        this.parent = organisasi;
        this.levelOrg = levelOrg;
        this.nama = nama;
        this.shortName = shortName;
    }

    public Organisasi(Long id, String kode, Organisasi organisasi, Integer levelOrg, String nama, String shortName, String category) {
        super(id);
        this.kode = kode;
        this.parent = organisasi;
        this.levelOrg = levelOrg;
        this.nama = nama;
        this.shortName = shortName;
        this.category = category;
    }

    public Organisasi(String nama) {
        this.nama = nama;
    }
}
