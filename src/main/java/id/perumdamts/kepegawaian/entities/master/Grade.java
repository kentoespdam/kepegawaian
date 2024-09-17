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
        @Index(columnList = "grade"),
        @Index(columnList = "is_deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE grade SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted <> 1")
@EqualsAndHashCode(callSuper = true)
@Audited
public class Grade extends IdsAbstract {
    @NotAudited
    @ManyToOne
    @JoinColumn(name = "level_id", referencedColumnName = "id")
    private Level level;
    private Integer grade;
    private Double tukin;
    private Boolean isDeleted = false;

    public Grade(Long id) {
        super(id);
    }
}
