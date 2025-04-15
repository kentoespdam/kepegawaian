package id.perumdamts.kepegawaian.entities.profil;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
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
        @Index(columnList = "is_deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE pengalaman_kerja SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class PengalamanKerja extends IdsAbstract {
    @ManyToOne
    @JoinColumn(name = "biodata_id", referencedColumnName = "nik")
    private Biodata biodata;
    private String namaPerusahaan;
    private String typePerusahaan;
    private String jabatan;
    private String lokasi;
    private Integer tahunMasuk;
    private Integer tahunKeluar;
    private String notes;
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
