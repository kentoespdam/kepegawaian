package id.perumdamts.kepegawaian.entities.master;

import id.perumdamts.kepegawaian.entities.commons.MasterBaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "Jenis_pelatihan", indexes = {
        @Index(columnList = "is_deleted")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE jenis_pelatihan SET is_deleted = TRUE WHERE id = ?")
public class JenisPelatihan extends MasterBaseEntity {
    private String nama;
}
