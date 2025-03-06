package id.perumdamts.kepegawaian.entities.penggajian;


import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serializable;

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
public class GajiBatchMaster implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //    @ManyToOne
//    @JoinColumn(name = "root_batch_id", referencedColumnName = "batch_id")
//    private GajiBatchRoot gajiBatchRoot;
    private String rootBatchId;
    private String periode;
    private Long pegawaiId;
    private String nipam;
    private String nama;
    private Long jabatanId;
    private String namaJabatan;
    private Long levelId;
    private Long organisasiId;
    private String namaOrganisasi;
    private Long golonganId;
    private String golongan;
    private String pangkat;
    @Enumerated(EnumType.ORDINAL)
    private EStatusPegawai statusPegawai;
    private Long gajiProfilId;
    private GajiPendapatanNonPajak gajiPendapatanNonPajakId;
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
