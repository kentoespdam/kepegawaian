package id.perumdamts.kepegawaian.entities.master;

import com.fasterxml.jackson.annotation.JsonBackReference;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(indexes = {
        @Index(columnList = "nama"),
        @Index(columnList = "is_deleted")
})
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class Apd extends IdsAbstract {
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "profesi_id", referencedColumnName = "id")
    private Profesi profesi;
    private String nama;
}
