package id.perumdamts.kepegawaian.entities.profil;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EGolonganDarah;
import id.perumdamts.kepegawaian.entities.commons.EJenisKelamin;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(columnList = "nama"),
        @Index(columnList = "is_deleted"),
        @Index(columnList = "jenis_kelamin"),
        @Index(columnList = "alamat"),
        @Index(columnList = "isPegawai")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE biodata SET is_deleted = TRUE WHERE nik=?")
@SQLRestriction("is_deleted = FALSE")
@EntityListeners(AuditingEntityListener.class)
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class Biodata implements Serializable {
    @Id
    @NotEmpty
    private String nik;
    private String nama;
    @Enumerated(EnumType.ORDINAL)
    private EJenisKelamin jenisKelamin;
    private String tempatLahir;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalLahir;
    private String alamat;
    private String telp;
    @Enumerated(EnumType.ORDINAL)
    private EAgama agama;
    private String ibuKandung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pendidikan_id", referencedColumnName = "id")
    private JenjangPendidikan pendidikanTerakhir;
    @Enumerated(EnumType.STRING)
    private EGolonganDarah golonganDarah;
    @Enumerated(EnumType.ORDINAL)
    private EStatusKawin statusKawin;
    private String fotoProfil;
    private String notes;

    @JsonBackReference
    @OneToMany(mappedBy = "biodata")
    private List<KartuIdentitas> kartuIdentitas;
    private Boolean isPegawai = false;
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    @LastModifiedBy
    private String updatedBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @LastModifiedDate
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
    private Boolean isDeleted = false;
    @Version
    private Long version = 1L;

    @JsonManagedReference
    @OneToMany(mappedBy = "biodata")
    List<Pendidikan> pendidikanList=new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "biodata")
    List<KartuIdentitas> kartuIdentitasList=new ArrayList<>();

    public Biodata(String nik) {
        this.nik = nik;
    }

    @Override
    public String toString() {
        return "Biodata{" +
                "nik='" + nik + '\'' +
                ", nama='" + nama + '\'' +
                ", jenisKelamin=" + jenisKelamin +
                ", tempatLahir='" + tempatLahir + '\'' +
                ", tanggalLahir=" + tanggalLahir +
                ", alamat='" + alamat + '\'' +
                ", telp='" + telp + '\'' +
                ", agama=" + agama +
                ", ibuKandung='" + ibuKandung + '\'' +
                ", golonganDarah=" + golonganDarah +
                ", statusKawin=" + statusKawin +
                ", fotoProfil='" + fotoProfil + '\'' +
                ", notes='" + notes + '\'' +
                ", isPegawai=" + isPegawai +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                ", updatedBy='" + updatedBy + '\'' +
                ", updatedAt=" + updatedAt +
                ", isDeleted=" + isDeleted +
                ", version=" + version +
                '}';
    }
}
