package id.perumdamts.kepegawaian.entities.master;

import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;

@Entity
@Table(indexes = {
        @Index(columnList = "golongan"),
        @Index(columnList = "pangkat"),
        @Index(columnList = "is_deleted")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE golongan SET is_deleted = TRUE WHERE id=?")
@SQLRestriction("is_deleted = FALSE")
@Audited
public class Golongan extends IdsAbstract {
    private String golongan;
    private String pangkat;

    public Golongan(Long id) {
        super(id);
    }

    public Golongan(Long id, String golongan, String pangkat) {
        super(id);
        this.golongan = golongan;
        this.pangkat = pangkat;
    }
}
