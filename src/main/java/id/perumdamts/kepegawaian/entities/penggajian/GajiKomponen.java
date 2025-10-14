package id.perumdamts.kepegawaian.entities.penggajian;

import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Table(indexes = {
        @Index(columnList = "is_deleted")
})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE gaji_komponen SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted = false")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class GajiKomponen extends IdsAbstract {
    private Integer urut;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profil_gaji_id", referencedColumnName = "id")
    private GajiProfil profilGaji;
    private String kode;
    private String nama;
    @Enumerated(EnumType.STRING)
    private EJenisGaji jenisGaji;
    private Double nilai;
    // if isReference true, formula will be ignored because data come from another table
    private Boolean isReference = false;
    private String formula;

    public GajiKomponen(int urut, GajiProfil gajiProfil, String kode, String nama, EJenisGaji jenisGaji, double nilai, Boolean isReference, String formula) {
        this.urut = urut;
        this.profilGaji = gajiProfil;
        this.kode = kode;
        this.nama = nama;
        this.jenisGaji = jenisGaji;
        this.nilai = nilai;
        this.isReference = isReference;
        this.formula = formula;
    }
}
