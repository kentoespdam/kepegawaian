package id.perumdamts.kepegawaian.entities.master;

import id.perumdamts.kepegawaian.entities.commons.MasterBaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

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
public class JenisKitas extends MasterBaseEntity {
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
