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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE rumah_dinas SET is_deleted=true WHERE id=?")
public class RumahDinas extends MasterBaseEntity {
    private String nama;
    private Double nilai;

    public RumahDinas(Long id, String nama, double nilai) {
        super(id);
        this.nama = nama;
        this.nilai = nilai;
    }
}
