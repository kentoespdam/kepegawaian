package id.perumdamts.kepegawaian.entities.penggajian;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(indexes = {
        @Index(columnList = "periode"),
        @Index(columnList = "nipam"),
        @Index(columnList = "nama")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GajiBatchMaster implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "batch_root_id", referencedColumnName = "id")
    @JsonBackReference
    @JsonIdentityReference(alwaysAsId = true)
    private GajiBatchRoot gajiBatchRoot;
//    private Long batch_root_id;
    private String periode;
    private Long pegawaiId;
    private String nipam;
    private String nama;
    private Long jabatanId;
    private String namaJabatan;
    private Long levelId;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "organisasi_id", referencedColumnName = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Organisasi organisasi;
    private String namaOrganisasi;
    private Long golonganId;
    private String golongan;
    private String pangkat;
    @Enumerated(EnumType.ORDINAL)
    private EStatusPegawai statusPegawai;
    private Long gajiProfilId;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "gaji_pendapatan_non_pajak_id", referencedColumnName = "id")
    @JsonIdentityReference(alwaysAsId = true)
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
