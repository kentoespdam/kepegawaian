package id.perumdamts.kepegawaian.entities.kepegawaian;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Table(indexes = {
        @Index(columnList = "nomor_kontrak"),
        @Index(columnList = "tanggal_mulai"),
        @Index(columnList = "is_deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE riwayat_kontrak SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted = false")
@EqualsAndHashCode(callSuper = true)
public class RiwayatKontrak extends IdsAbstract {
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="pegawai_id", referencedColumnName = "id")
    private Pegawai pegawai;
    private String nomor_kontrak;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggal_mulai;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggal_selesai;
    private String notes;
}
