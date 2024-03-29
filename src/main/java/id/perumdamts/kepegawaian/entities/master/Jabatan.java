package id.perumdamts.kepegawaian.entities.master;

import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.*;
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
@SQLDelete(sql = "UPDATE jabatan SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted <> 1")
@EqualsAndHashCode(callSuper = true)
public class Jabatan extends IdsAbstract {
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Jabatan jabatan;
    @ManyToOne
    @JoinColumn(name = "organisasi_id")
    private Organisasi organisasi;
    @ManyToOne
    @JoinColumn(name = "level_id")
    private Level level;
    private String nama;
    @ManyToOne
    @JoinColumn(name = "golongan_id")
    private Golongan golongan;

    public Jabatan(Long id) {
        super(id);
    }
}
