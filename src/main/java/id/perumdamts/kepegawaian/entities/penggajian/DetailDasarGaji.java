package id.perumdamts.kepegawaian.entities.penggajian;

import com.fasterxml.jackson.annotation.JsonBackReference;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Table(indexes = {
        @Index(columnList = "mkg"),
        @Index(columnList = "is_deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE dasar_gaji SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted = false")
@EqualsAndHashCode(callSuper = true)
@Audited
public class DetailDasarGaji extends IdsAbstract {
    @NotAudited
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "dasar_gaji_id", referencedColumnName = "id")
    private DasarGaji dasarGaji;
    private Integer mkg;
    private Integer golonganKode;
    private Double nominal;

    public DetailDasarGaji(Long id, DasarGaji dasarGaji, Integer mkg, Integer golonganKode, Double nominal) {
        super(id);
        this.dasarGaji = dasarGaji;
        this.mkg = mkg;
        this.golonganKode = golonganKode;
        this.nominal = nominal;
    }
}
