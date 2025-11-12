package id.perumdamts.kepegawaian.entities.profil;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.commons.*;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.time.LocalDate;

@Entity
@Table(name = "ProfilKeluarga", indexes = {
        @Index(name = "idx_profilkeluarga_nik", columnList = "nik"),
        @Index(name = "idx_profilkeluarga_nama", columnList = "nama"),
        @Index(name = "idx_profilkeluarga_is_deleted", columnList = "is_deleted"),
        @Index(name = "idx_profilkeluarga_tanggungan", columnList = "tanggungan")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uc_profilkeluarga_nik", columnNames = {"biodata_id", "version", "nama", "tanggal_lahir", "is_deleted"})
})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE profil_keluarga SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class ProfilKeluarga extends IdsAbstract {
    private String nik;
    private String nama;
    @Enumerated(EnumType.ORDINAL)
    private EJenisKelamin jenisKelamin;
    @Enumerated(EnumType.ORDINAL)
    private EAgama agama;
    @Enumerated(EnumType.ORDINAL)
    private EHubunganKeluarga hubunganKeluarga;
    private String tempatLahir;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalLahir;
    @Column(name = "tanggungan", columnDefinition = "boolean default false")
    private Boolean tanggungan = false;
    @ManyToOne
    @JoinColumn(name = "pendidikan_id", referencedColumnName = "id")
    private JenjangPendidikan pendidikan;
    @Enumerated(EnumType.ORDINAL)
    private EStatusPendidikan statusPendidikan;
    private Boolean statusKawin;
    private String notes;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "biodata_id", referencedColumnName = "nik")
    @JsonIgnore
    private Biodata biodata;
    @Column(columnDefinition = "boolean default false")
    private Boolean changedStatus;
}
