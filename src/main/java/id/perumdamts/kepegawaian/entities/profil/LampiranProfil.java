package id.perumdamts.kepegawaian.entities.profil;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Table(indexes = {
        @Index(columnList = "is_deleted"),
        @Index(columnList = "ref"),
        @Index(columnList = "ref_id")
})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

@SQLDelete(sql = "UPDATE lampiran_profil SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@Audited
public class LampiranProfil extends IdsAbstract {
    @Enumerated(EnumType.ORDINAL)
    private EJenisLampiranProfil ref;
    private Long refId;
    private String mimeType;
    private String fileName;
    private String notes;
    private String hashedFileName;
    private Boolean disetujui;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tanggalPengajuan;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tanggalDisetujui;
    private String disetujuiOleh;
}
