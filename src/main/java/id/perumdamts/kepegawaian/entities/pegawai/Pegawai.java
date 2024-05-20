package id.perumdamts.kepegawaian.entities.pegawai;

import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import id.perumdamts.kepegawaian.entities.master.*;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(indexes = {
        @Index(columnList = "nipam", unique = true),
        @Index(columnList = "is_deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE pegawai SET is_deleted = true WHERE id = ?")
//@SQLRestriction("is_deleted = FALSE")
@EqualsAndHashCode(callSuper = true)
public class Pegawai extends IdsAbstract {
    @NotEmpty
    @Column(unique = true)
    private String nipam;
    @OneToOne
    @JoinColumn(name = "biodata_id", unique = true, referencedColumnName = "nik")
    private Biodata biodata;
    @ManyToOne
    @JoinColumn(name = "status_pegawai_id", referencedColumnName = "id")
    private StatusPegawai statusPegawai;
    @ManyToOne
    @JoinColumn(name = "jabatan_id", referencedColumnName = "id")
    private Jabatan jabatan;
    @ManyToOne
    @JoinColumn(name = "organisasi_id", referencedColumnName = "id")
    private Organisasi organisasi;
    @ManyToOne
    @JoinColumn(name = "profesi_id", referencedColumnName = "id")
    private Profesi profesi;
    @ManyToOne
    @JoinColumn(name = "golongan_id", referencedColumnName = "id")
    private Golongan golongan;
    @ManyToOne
    @JoinColumn(name = "grade_id", referencedColumnName = "id")
    private Grade grade;
    @ManyToOne
    @JoinColumn(name = "status_kerja_id", referencedColumnName = "id")
    private StatusKerja statusKerja;
    private String notes;
}
