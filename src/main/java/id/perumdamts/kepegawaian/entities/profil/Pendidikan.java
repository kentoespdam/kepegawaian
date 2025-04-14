package id.perumdamts.kepegawaian.entities.profil;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.time.LocalDateTime;


@Entity
@Table(indexes = {
        @Index(columnList = "jenjang_id"),
        @Index(columnList = "is_deleted"),
        @Index(columnList = "is_latest"),
        @Index(columnList = "disetujuiOleh")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE pendidikan SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@EqualsAndHashCode(callSuper = true)
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class Pendidikan extends IdsAbstract {
    @ManyToOne
    @JoinColumn(name = "biodata_id", referencedColumnName = "nik")
    private Biodata biodata;
    @ManyToOne
    @JoinColumn(name = "jenjang_id", referencedColumnName = "id")
    private JenjangPendidikan jenjangPendidikan;
    private String gelarDepan;
    private String gelarBelakang;
    private String jurusan;
    private String institusi;
    private String kota;
    private Integer tahunMasuk;
    private Boolean lulus;
    private Integer tahunLulus;
    private Double gpa;
    @Column(columnDefinition = "boolean default false")
    private Boolean isLatest;
    @Column(columnDefinition = "boolean default false")
    private Boolean disetujui;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tanggalPengajuan;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tanggalDisetujui;
    private String disetujuiOleh;
}
