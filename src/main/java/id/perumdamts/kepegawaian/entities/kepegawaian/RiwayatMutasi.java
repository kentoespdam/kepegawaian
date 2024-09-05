package id.perumdamts.kepegawaian.entities.kepegawaian;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
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
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;

@Entity
@Table(indexes = {
        @Index(columnList = "is_deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE riwayat_mutasi SET is_deleted=true WHERE id=?")
@SQLRestriction("is_deleted = false")
@EqualsAndHashCode(callSuper = true)
public class RiwayatMutasi extends IdsAbstract {
    @NotAudited
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="pegawai_id", referencedColumnName = "id")
    private Pegawai pegawai;
    @NotAudited
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "riwayat_sk_id", referencedColumnName = "id")
    private RiwayatSk riwayatSk;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtBerlaku;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tglBerakhir;
    @Enumerated(EnumType.ORDINAL)
    private EJenisMutasi jenisMutasi;
    @NotAudited
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="organisasi_id", referencedColumnName = "id")
    private Organisasi organisasi;
    private String namaOrganisasi;
    @NotAudited
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="jabatan_id", referencedColumnName = "id")
    private Jabatan jabatan;
    private String namaJabatan;
    @NotAudited
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="organisasi_lama_id", referencedColumnName = "id")
    private Organisasi organisasiLama;
    private String namaOrganisasiLama;
    @NotAudited
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="jabatan_lama_id", referencedColumnName = "id")
    private Jabatan jabatanLama;
    private String namaJabatanLama;
    private String notes;
}
