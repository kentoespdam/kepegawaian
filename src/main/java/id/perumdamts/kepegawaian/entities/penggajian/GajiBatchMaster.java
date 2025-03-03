package id.perumdamts.kepegawaian.entities.penggajian;

import com.fasterxml.jackson.annotation.JsonBackReference;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(indexes = {
        @Index(columnList = "periode"),
        @Index(columnList = "nipam"),
        @Index(columnList = "nama"),
        @Index(columnList = "is_deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE gaji_master_batch SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted = false")
@EqualsAndHashCode(callSuper = true)
public class GajiBatchMaster extends IdsAbstract {
    @ManyToOne
    @JoinColumn(name = "root_batch_id", referencedColumnName = "batchId")
    private GajiBatchRoot gajiBatchRoot;
    private String periode;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "pegawai_id", referencedColumnName = "id")
    private Pegawai pegawai;
    private String nipam;
    private String nama;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "jabatan_id", referencedColumnName = "id")
    private Jabatan jabatan;
    private String namaJabatan;
    private Long levelId;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "organisasi_id", referencedColumnName = "id")
    private Organisasi organisasi;
    private String namaOrganisasi;
    private Long golonganId;
    private String golongan;
    private String pangkat;
    @Enumerated(EnumType.ORDINAL)
    private EStatusPegawai statusPegawai;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "gaji_profil_id", referencedColumnName = "id")
    private GajiProfil gajiProfil;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "gaji_pendapatan_non_pajak_id", referencedColumnName = "id")
    private GajiPendapatanNonPajak gajiPendapatanNonPajak;
    private String kodePajak;
    private Double gajiPokok;
    private Double phdp;
    @Enumerated(EnumType.ORDINAL)
    private EStatusKawin statusKawin;
    private Integer jmlTanggungan;
    private Integer jmlJiwa;
    private Double penghasilanKotor;
    private Double totalPotongan;
    private Double totalAddTambahan;
    private Double totalAddPotongan;
    private Double penghasilanBersih;
    private Double penghasilanBersih2;
    private Double pembulatan;
    private Double pembulatan2;
    private Double penghasilanBersihFinal;
    private Double penghasilanBersihFinal2;
    private Double pajak;
    private Boolean isDifferent = false;

}
