package id.perumdamts.kepegawaian.entities.master;

import id.perumdamts.kepegawaian.entities.commons.EJenisLibur;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

@Entity
@Table(indexes = {
        @Index(name = "is_deleted_idx", columnList = "is_deleted")
}, uniqueConstraints = {
        @UniqueConstraint(name = "unique_tanggal_idx", columnNames = {"tanggal"})})
@Data
@Audited
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE hari_libur SET is_deleted = true where id = ?")
@SQLRestriction("is_deleted = false")
@NoArgsConstructor
@AllArgsConstructor
public class HariLibur extends IdsAbstract {
    private LocalDate tanggal;
    @Enumerated(EnumType.ORDINAL)
    private EJenisLibur jenisLibur;
    @Column(columnDefinition = "TEXT")
    private String notes;

    public HariLibur(Long id, LocalDate tanggal, EJenisLibur jenisLibur, String notes) {
        super(id);
        this.tanggal = tanggal;
        this.jenisLibur = jenisLibur;
        this.notes = notes;
    }
}
