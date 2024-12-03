package id.perumdamts.kepegawaian.entities.master;

import com.fasterxml.jackson.annotation.JsonBackReference;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(indexes = {
        @Index(columnList = "is_deleted"),
        @Index(columnList = "nama")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlatKerja extends IdsAbstract {
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "profesi_id", referencedColumnName = "id")
    private Profesi profesi;
    private String nama;
}
