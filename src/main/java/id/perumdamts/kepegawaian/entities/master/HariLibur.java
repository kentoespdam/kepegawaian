package id.perumdamts.kepegawaian.entities.master;

import id.perumdamts.kepegawaian.entities.commons.EJenisLibur;
import id.perumdamts.kepegawaian.entities.commons.MasterBaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;

@Entity
@Table(indexes = {
        @Index(name = "is_deleted_idx", columnList = "is_deleted")
}, uniqueConstraints = {
        @UniqueConstraint(name = "unique_tanggal_idx", columnNames = {"tanggal"})})
@Getter
@Setter
@ToString
@SQLDelete(sql = "UPDATE hari_libur SET is_deleted = true where id = ?")
@NoArgsConstructor
@AllArgsConstructor
public class HariLibur extends MasterBaseEntity {
    private LocalDate tanggal;
    @Enumerated(EnumType.ORDINAL)
    private EJenisLibur jenisLibur;
    private String notes;
}
