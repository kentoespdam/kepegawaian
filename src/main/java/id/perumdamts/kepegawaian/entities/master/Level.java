package id.perumdamts.kepegawaian.entities.master;

import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(indexes = {
        @Index(columnList = "nama"),
        @Index(columnList = "is_deleted")
})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE level SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted <> 1")
public class Level extends IdsAbstract {
    private String nama;

    public Level(Long id) {
        super(id);
    }

    public Level(Long id, String nama) {
        super(id);
        this.nama = nama;
    }
}
