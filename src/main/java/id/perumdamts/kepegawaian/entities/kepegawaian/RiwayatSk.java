package id.perumdamts.kepegawaian.entities.kepegawaian;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import id.perumdamts.kepegawaian.entities.master.Golongan;
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
        @Index(columnList = "nomorSk"),
        @Index(columnList = "tanggalSk"),
        @Index(columnList = "mkgTahun"),
        @Index(columnList = "mkgbTahun"),
        @Index(columnList = "is_deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE riwayat_sk SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted = false")
@EqualsAndHashCode(callSuper = true)
public class RiwayatSk extends IdsAbstract {
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "pegawai_id", referencedColumnName = "id")
    private Pegawai pegawai;
    private String nomorSk;
    @Enumerated(EnumType.ORDINAL)
    private EJenisSk jenisSk;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalSk;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtBerlaku;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "golongan_id", referencedColumnName = "id")
    private Golongan golongan;
    private Double gajiPokok;
    private Integer mkgTahun;
    private Integer mkgBulan;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate kenaikanBerikutnya;
    private Integer mkgbTahun;
    private Integer mkgbBulan;
    private Boolean updateMaster;
    private String notes;
}
