package id.perumdamts.kepegawaian.entities.penggajian;

import com.fasterxml.jackson.annotation.JsonBackReference;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

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
public class DetailDasarGaji extends IdsAbstract {
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "dasar_gaji_id", referencedColumnName = "id")
    private DasarGaji dasarGaji;
    private Integer mkg;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "golongan_id", referencedColumnName = "id")
    private Golongan golongan;
    private Double nominal;

    public DetailDasarGaji(Long id, DasarGaji dasarGaji, Integer mkg, Golongan golongan, Double nominal) {
        super(id);
        this.dasarGaji = dasarGaji;
        this.mkg = mkg;
        this.golongan = golongan;
        this.nominal = nominal;
    }
}
