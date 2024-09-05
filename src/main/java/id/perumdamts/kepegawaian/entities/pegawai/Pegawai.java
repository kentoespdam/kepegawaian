package id.perumdamts.kepegawaian.entities.pegawai;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
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
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;

@Entity
@Table(indexes = {
        @Index(columnList = "nipam", unique = true),
        @Index(columnList = "is_deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE pegawai SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@EqualsAndHashCode(callSuper = true)
@Audited
public class Pegawai extends IdsAbstract {
    @NotEmpty
    @Column(unique = true)
    private String nipam;
    @NotAudited
    @OneToOne
    @JoinColumn(name = "biodata_id", unique = true, referencedColumnName = "nik")
    private Biodata biodata;
    @Enumerated(EnumType.ORDINAL)
    private EStatusPegawai statusPegawai;
    @NotAudited
    @ManyToOne
    @JoinColumn(name = "organisasi_id", referencedColumnName = "id")
    private Organisasi organisasi;
    @NotAudited
    @ManyToOne
    @JoinColumn(name = "jabatan_id", referencedColumnName = "id")
    private Jabatan jabatan;
    @NotAudited
    @ManyToOne
    @JoinColumn(name = "profesi_id", referencedColumnName = "id")
    private Profesi profesi;
    @NotAudited
    @ManyToOne
    @JoinColumn(name = "golongan_id", referencedColumnName = "id")
    private Golongan golongan;
    @NotAudited
    @ManyToOne
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

    private Double gajiPokok;
    private Double phdp;
    private Integer jmlTanggungan;

    private Integer mkgTahun;
    private Integer mkgBulan;

    private Long absensiId;
    private String notes;
}
