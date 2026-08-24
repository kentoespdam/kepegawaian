package id.perumdamts.kepegawaian.entities.master;

import id.perumdamts.kepegawaian.entities.commons.MasterBaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

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
public class Golongan extends MasterBaseEntity {
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
