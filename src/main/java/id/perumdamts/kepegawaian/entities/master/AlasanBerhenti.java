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
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE alasan_berhenti SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@Audited
public class AlasanBerhenti extends IdsAbstract {
    private String nama;
    private String notes;

    public AlasanBerhenti(Long id, String nama, String notes) {
        super(id);
        this.nama = nama;
        this.notes = notes;
    }
}
