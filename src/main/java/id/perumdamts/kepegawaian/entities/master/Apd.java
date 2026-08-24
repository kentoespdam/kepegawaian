package id.perumdamts.kepegawaian.entities.master;

import com.fasterxml.jackson.annotation.JsonBackReference;
import id.perumdamts.kepegawaian.entities.commons.MasterBaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(indexes = {
        @Index(columnList = "nama"),
        @Index(columnList = "is_deleted")
})
@SQLDelete(sql = "UPDATE apd SET is_deleted = true WHERE id = ?")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Apd extends MasterBaseEntity {
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "profesi_id", referencedColumnName = "id")
    private Profesi profesi;
    private String nama;
}
