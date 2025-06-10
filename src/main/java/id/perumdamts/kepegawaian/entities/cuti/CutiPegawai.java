package id.perumdamts.kepegawaian.entities.cuti;

import com.fasterxml.jackson.annotation.JsonBackReference;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EJenisPengajuanCuti;
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
import org.hibernate.envers.Audited;

import java.time.LocalDate;

@Entity
@Table(indexes = {
        @Index(name = "is_deleted_idx", columnList = "is_deleted")
})
@Data
@Audited
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE cuti_pegawai SET is_deleted = true where id = ?")
@SQLRestriction("WHERE is_deleted = false")
@NoArgsConstructor
@AllArgsConstructor
public class CutiPegawai extends IdsAbstract {
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "pegawai_id", referencedColumnName = "id")
    private Pegawai pegawai;
    private String nipam;
    private String nama;
    @ManyToOne
    @JoinColumn(name = "organisasi_id", referencedColumnName = "id")
    private Organisasi organisasi;
    @ManyToOne
    @JoinColumn(name = "jabatan_id", referencedColumnName = "id")
    private Jabatan jabatan;
    @Enumerated(EnumType.ORDINAL)
    private EJenisPengajuanCuti jenisPengajuanCuti;
    @ManyToOne
    @JoinColumn(name = "ref_cuti_id", referencedColumnName = "id")
    private CutiPegawai refCuti;
    @ManyToOne
    @JoinColumn(name = "jenis_cuti_id", referencedColumnName = "id")
    private CutiJenis jenisCuti;
    private LocalDate tanggalMulai;
    private LocalDate tanggalSelesai;
    private Integer jumlahHari;
    private Integer jumlahHariKerja;
    private Integer kuotaAwal;
    private Integer kuotaAkhir;
    private String alasan;
    @Enumerated(EnumType.ORDINAL)
    private EApprovalCutiStatus approvalCutiStatus = EApprovalCutiStatus.PENDING;
    private Integer approvalLevel;

}
