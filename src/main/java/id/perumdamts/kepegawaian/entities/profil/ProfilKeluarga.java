package id.perumdamts.kepegawaian.entities.profil;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        @Index(columnList = "nik", name = "idx_profilkeluarga_nik"),
        @Index(columnList = "nama", name = "idx_profilkeluarga_nama"),
        @Index(columnList = "is_deleted", name = "idx_profilkeluarga_is_deleted"),
        @Index(columnList = "tanggungan", name = "idx_profilkeluarga_tanggungan"),
        @Index(columnList = "lta_tag", name = "idx_profilkeluarga_lta_tag")
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
    @ManyToOne
    @JoinColumn(name = "biodata_id", referencedColumnName = "nik")
    private Biodata biodata;
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
    @Column(name = "changed_status", columnDefinition = "boolean default false")
    private Boolean changedStatus;
}
