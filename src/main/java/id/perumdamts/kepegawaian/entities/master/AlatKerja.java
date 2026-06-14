package id.perumdamts.kepegawaian.entities.master;

import com.fasterxml.jackson.annotation.JsonBackReference;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;

@Entity
@Table(indexes = {
        @Index(columnList = "is_deleted"),
        @Index(columnList = "nama")
})
@SQLDelete(sql = "UPDATE alat_kerja SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@Audited
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class AlatKerja extends IdsAbstract {
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "profesi_id", referencedColumnName = "id")
    private Profesi profesi;
    private String nama;
}
