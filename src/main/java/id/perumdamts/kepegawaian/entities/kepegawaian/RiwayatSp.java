package id.perumdamts.kepegawaian.entities.kepegawaian;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.commons.EJenisSp;
import id.perumdamts.kepegawaian.entities.commons.IdsAbstract;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
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
        @Index(columnList = "nomorSp"),
        @Index(columnList = "tglSp"),
        @Index(columnList = "is_deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE riwayat_sp SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted = false")
@EqualsAndHashCode(callSuper = true)
public class RiwayatSp extends IdsAbstract {
    private String nomorSp;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "pegawai_id", referencedColumnName = "id")
    private Pegawai pegawai;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "organisasi_id", referencedColumnName = "id")
    private Organisasi organisasi;
    private String namaOrganisasi;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "jabatan_id", referencedColumnName = "id")
    private Jabatan jabatan;
    private String namaJabatan;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tglSp;
    @Enumerated(EnumType.ORDINAL)
    private EJenisSp jenisSp;
    private String notes;
}
