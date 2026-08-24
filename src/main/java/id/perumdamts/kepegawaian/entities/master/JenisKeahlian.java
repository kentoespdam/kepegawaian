package id.perumdamts.kepegawaian.entities.master;

import id.perumdamts.kepegawaian.entities.commons.MasterBaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(indexes = {
        @Index(columnList = "is_deleted")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE jenis_keahlian SET is_deleted = TRUE WHERE id = ?")
public class JenisKeahlian extends MasterBaseEntity {
    private String nama;

    public JenisKeahlian(Long id, String nama) {
        super(id);
        this.nama = nama;
    }
}
