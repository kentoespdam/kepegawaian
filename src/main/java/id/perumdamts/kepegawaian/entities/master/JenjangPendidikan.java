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
@SQLDelete(sql = "UPDATE jenjang_pendidikan SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
public class JenjangPendidikan extends IdsAbstract {
    private String nama;
    private String shortName;
    private Integer seq;
    private Boolean isStatistik = Boolean.FALSE;

    public JenjangPendidikan(Long pendidikanId) {
        super(pendidikanId);
    }

    public JenjangPendidikan(Long id, String nama, String shortName, Integer seq, Boolean isStatistik) {
        super(id);
        this.nama = nama;
        this.shortName = shortName;
        this.seq = seq;
        this.isStatistik = isStatistik;
    }
}
