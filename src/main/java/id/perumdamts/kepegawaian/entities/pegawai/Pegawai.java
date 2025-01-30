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
@Table(
        name = "pegawai",
        indexes = {
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
    @Column(name = "nipam", unique = true, columnDefinition = "VARCHAR(9)")
    private String nipam;
    @JsonBackReference
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nik", unique = true, referencedColumnName = "nik")
    private Biodata biodata;
    @Column(name = "status_pegawai", nullable = false)
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

    @Column(name = "status_kerja", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private EStatusKerja statusKerja;

    @Column(name = "ref_sk_capeg_id")
    private Long refSkCapegId;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "tmt_kerja")
    private LocalDate tmtKerja;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "tmt_pensiun")
    private LocalDate tmtPensiun;

    @Column(name = "ref_sk_pegawai_id")
    private Long refSkPegawaiId;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "tmt_pegawai")
    private LocalDate tmtPegawai;

    @Column(name = "ref_sk_gol_id")
    private Long refSkGolId;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "tmt_golongan")
    private LocalDate tmtGolongan;

    @Column(name = "ref_sk_jabatan_id")
    private Long refSkJabatanId;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "tmt_jabatan")
    private LocalDate tmtJabatan;

    @Column(name = "ref_sk_mutasi_id")
    private Long refSkMutasiId;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "tmt_mutasi")
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
    @Column(name = "gaji_pokok")
    private Double gajiPokok;
    @Column(name = "is_askes")
    private Boolean isAskes = false;
    @Column(name = "phdp")
    private Double phdp;
    @Column(name = "jmlTanggungan")
    private Integer jmlTanggungan;

    @Column(name = "mkg_tahun")
    private Integer mkgTahun;
    @Column(name = "mkg_bulan")
    private Integer mkgBulan;

    @Column(name = "absensi_id")
    private Long absensiId;
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @JsonManagedReference
    @OneToMany(mappedBy = "pegawai")
    private List<RiwayatSk> riwayatSkList;

    @JsonManagedReference
    @OneToMany(mappedBy = "pegawai")
    private List<RiwayatMutasi> riwayatMutasiList;
}
