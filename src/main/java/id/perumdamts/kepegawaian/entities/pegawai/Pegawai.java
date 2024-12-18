package id.perumdamts.kepegawaian.entities.pegawai;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.master.*;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(columnList = "nipam", unique = true),
        @Index(columnList = "tmt_pensiun"),
        @Index(columnList = "is_deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE pegawai SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@EqualsAndHashCode(callSuper = true)
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class Pegawai extends IdsAbstract {
    @NotEmpty
    @Column(unique = true)
    private String nipam;
    @JsonBackReference
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "biodata_id", unique = true, referencedColumnName = "nik")
    private Biodata biodata;
    @Enumerated(EnumType.ORDINAL)
    private EStatusPegawai statusPegawai;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "organisasi_id", referencedColumnName = "id")
    private Organisasi organisasi;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "jabatan_id", referencedColumnName = "id")
    private Jabatan jabatan;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesi_id", referencedColumnName = "id")
    private Profesi profesi;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "golongan_id", referencedColumnName = "id")
    private Golongan golongan;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", referencedColumnName = "id")
    private Grade grade;

    @Enumerated(EnumType.ORDINAL)
    private EStatusKerja statusKerja;

    private Long refSkCapegId;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtKerja;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtPensiun;

    private Long refSkPegawaiId;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtPegawai;

    private Long refSkGolId;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtGolongan;

    private Long refSkJabatanId;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtJabatan;

    private Long refSkMutasiId;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtMutasi;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gaji_profil_id", referencedColumnName = "id")
    private GajiProfil gajiProfil;
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gaji_pendapatan_non_pajak_id", referencedColumnName = "id")
    private GajiPendapatanNonPajak kodePajak;
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rumah_dinas_id", referencedColumnName = "id")
    private RumahDinas rumahDinas;
    private Double gajiPokok;
    private Boolean isAskes = false;
    private Double phdp;
    private Integer jmlTanggungan;

    private Integer mkgTahun;
    private Integer mkgBulan;

    private Long absensiId;
    private String notes;

    @JsonManagedReference
    @OneToMany(mappedBy = "pegawai")
    private List<RiwayatSk> riwayatSkList;

    @JsonManagedReference
    @OneToMany(mappedBy = "pegawai")
    private List<RiwayatMutasi> riwayatMutasiList;
}
