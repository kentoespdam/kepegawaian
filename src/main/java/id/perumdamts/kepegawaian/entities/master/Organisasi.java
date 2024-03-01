package id.perumdamts.kepegawaian.entities.master;

import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE organisasi SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted <> 1")
@EqualsAndHashCode(callSuper = true)
public class Organisasi extends IdsAbstract {
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Organisasi organisasi;
    private Integer levelOrg;
    private String nama;

    public Organisasi(Long id){
        super(id);
    }
}