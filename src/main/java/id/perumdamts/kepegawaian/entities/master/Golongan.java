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
@Table(indexes = @Index(columnList = "nama"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE golongan SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted <> 1")
@EqualsAndHashCode(callSuper = true)
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
