package id.perumdamts.kepegawaian.entities.profil;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import id.perumdamts.kepegawaian.entities.master.JenisPelatihan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(indexes = {
        @Index(columnList = "is_deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE pelatihan SET is_deleted = TRUE WHERE id=?")
@SQLRestriction("is_deleted = FALSE")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class Pelatihan extends IdsAbstract {
    @ManyToOne
    @JoinColumn(name = "biodata_id", referencedColumnName = "nik")
    private Biodata biodata;
    @ManyToOne
    @JoinColumn(name = "jenis_pelatihan_id", referencedColumnName = "id")
    private JenisPelatihan jenisPelatihan;
    private String nama;
    private String lembaga;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalMulai;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalSelesai;
    private Boolean lulus;
    private String nilai;
    private Boolean ikatanDinas = false;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalAkhirIkatan;
    private String notes;
    private Boolean disetujui;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tanggalPengajuan;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tanggalDisetujui;
    private String disetujuiOleh;
}
