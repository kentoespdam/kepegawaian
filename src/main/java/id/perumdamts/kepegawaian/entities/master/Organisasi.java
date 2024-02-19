package id.perumdamts.kepegawaian.entities.master;

import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE organisasi SET is_deleted = true WHERE id=?")
public class Organisasi extends IdsAbstract {
    private String nama;
}
