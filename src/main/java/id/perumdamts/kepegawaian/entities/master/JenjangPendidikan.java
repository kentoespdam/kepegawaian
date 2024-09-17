package id.perumdamts.kepegawaian.entities.master;

import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(indexes = {
        @Index(columnList = "nama"),
        @Index(columnList = "is_deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE jenjang_pendidikan SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@EqualsAndHashCode(callSuper = true)
public class JenjangPendidikan extends IdsAbstract {
    private String nama;
    private Integer seq;
    private Boolean isDeleted = false;

    public JenjangPendidikan(Long pendidikanId) {
        super(pendidikanId);
    }
}
