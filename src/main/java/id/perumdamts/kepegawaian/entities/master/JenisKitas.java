package id.perumdamts.kepegawaian.entities.master;

import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;

@Entity
@Table(indexes = {
        @Index(columnList = "nama"),
        @Index(columnList = "is_deleted")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE jenis_kitas SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")

@Audited
public class JenisKitas extends IdsAbstract {
    private String nama;

    public JenisKitas(Long jenisKitasId) {
        super(jenisKitasId);
    }

    public JenisKitas(Long id, String nama) {
        super(id);
        this.nama = nama;
    }

    @Override
    public String toString() {
        return "JenisKitas(" +
                "id=" + getId() + ", " +
                "nama='" + nama + '\'' +
                ") ";
    }
}
