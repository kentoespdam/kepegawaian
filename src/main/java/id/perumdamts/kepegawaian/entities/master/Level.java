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
@Table(name = "level",
        indexes = {
                @Index(columnList = "nama", unique = true),
                @Index(columnList = "is_deleted"),
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE `level` SET is_deleted=true WHERE id=?")
public class Level extends MasterBaseEntity {
    private String nama;

    public Level(Long id) {
        super(id);
    }

    public Level(Long id, String nama) {
        super(id);
        this.nama = nama;
    }
}
