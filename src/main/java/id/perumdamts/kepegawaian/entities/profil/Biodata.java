package id.perumdamts.kepegawaian.entities.profil;

import com.fasterxml.jackson.annotation.JsonFormat;
import id.perumdamts.kepegawaian.entities.commons.EAgama;
import id.perumdamts.kepegawaian.entities.commons.EGolonganDarah;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(columnList = "nama"),
        @Index(columnList = "is_deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE biodata SET is_deleted = TRUE WHERE nik=?")
@SQLRestriction("is_deleted = FALSE")
@EntityListeners(AuditingEntityListener.class)
public class Biodata implements Serializable {
    @Id
    @NotEmpty
    private String nik;
    private String nama;
    private String tempatLahir;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalLahir;
    private String alamat;
    private String telp;
    @Enumerated(value = EnumType.ORDINAL)
    private EAgama agama;
    private String ibuKandung;
    @ManyToOne
    @JoinColumn(name = "pendidikan_id")
    private JenjangPendidikan pendidikanTerakhir;
    @Enumerated(value = EnumType.STRING)
    private EGolonganDarah golonganDarah;
    @Enumerated(EnumType.ORDINAL)
    private EStatusKawin statusKawin;
    private String fotoProfil;
    private String notes;
    @OneToMany(mappedBy = "biodata")
    private List<KartuIdentitas> kartuIdentitas;
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedBy
    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @LastModifiedDate
    private LocalDateTime updatedAt;
    private Boolean isDeleted = false;

    public Biodata(String nik) {
        this.nik = nik;
    }
}
