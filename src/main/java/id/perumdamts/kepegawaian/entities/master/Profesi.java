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

import java.util.List;

@Entity
@Table(indexes = {
        @Index(columnList = "nama"),
        @Index(columnList = "is_deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE profesi SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted <> 1")
@EqualsAndHashCode(callSuper = true)
@Audited
public class Profesi extends IdsAbstract {
    @NotAudited
    @ManyToOne
    @JoinColumn(name = "level_id", referencedColumnName = "id")
    private Level level;
    private String nama;
    private String detail;
    private String resiko;
    private Boolean isDeleted = false;

    @NotAudited
    @OneToMany(mappedBy = "profesi")
    private List<Apd> apdList;
    @NotAudited
    @OneToMany(mappedBy = "profesi")
    private List<AlatKerja> alatKerjaList;

    public Profesi(Long id) {
        super(id);
    }

    public Profesi(Long id, Level level, String nama, String detail, String resiko) {
        super(id);
        this.level = level;
        this.nama = nama;
        this.detail = detail;
        this.resiko = resiko;
    }
}
