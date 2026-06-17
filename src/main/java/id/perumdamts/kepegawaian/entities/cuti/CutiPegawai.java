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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.time.LocalDate;

@Entity
@Table(indexes = {
        @Index(name = "is_deleted_idx", columnList = "is_deleted")
})
@Getter
@Setter
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@SQLDelete(sql = "UPDATE cuti_pegawai SET is_deleted = true where id = ?")
@SQLRestriction("is_deleted = FALSE")
@NoArgsConstructor
@AllArgsConstructor
public class CutiPegawai extends IdsAbstract {
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pegawai_id", referencedColumnName = "id")
    private Pegawai pegawai;
    private String nipam;
    private String nama;
    private String pangkatGolongan;
    @ManyToOne
    @JoinColumn(name = "organisasi_id", referencedColumnName = "id")
    private Organisasi organisasi;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "jabatan_id", referencedColumnName = "id")
    private Jabatan jabatan;
    @Enumerated(EnumType.ORDINAL)
    private EJenisPengajuanCuti jenisPengajuanCuti;
    @ManyToOne
    @JoinColumn(name = "ref_cuti_id", referencedColumnName = "id")
    @NotFound(action = NotFoundAction.IGNORE)
    private CutiPegawai refCuti;
    @ManyToOne
    @JoinColumn(name = "jenis_cuti_id", referencedColumnName = "id")
    private CutiJenis jenisCuti;
    @ManyToOne
    @JoinColumn(name = "sub_jenis_cuti_id", referencedColumnName = "id")
    private CutiJenis subJenisCuti;
    private LocalDate tanggalMulai;
    private LocalDate tanggalSelesai;
    private Integer jumlahHari = 0;
    private Integer jumlahHariKerja = 0;
    private Integer kuotaAwal = 0;
    private Integer kuotaAkhir = 0;
    private String alasan;
    @Enumerated(EnumType.ORDINAL)
    private EApprovalCutiStatus approvalCutiStatus = EApprovalCutiStatus.PENDING;
    private Integer approvalLevel;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pic_saat_ini_id", referencedColumnName = "id")
    private Jabatan picSaatIni;
    private Integer riwayatKuota0 = 0;
    private Integer riwayatKuota1 = 0;
    private Integer riwayatPakai0 = 0;
    private Integer riwayatPakai1 = 0;
    private Integer riwayatSisa0 = 0;
    private Integer riwayatSisa1 = 0;
    @Column(columnDefinition = "boolean default false")
    private Boolean isClaimed = false;

    public CutiPegawai(long id) {
        super(id);
    }

    @Override
    public String toString() {
        return "CutiPegawai{" +
                "pegawai=Pegawai( id=" + pegawai.getId() +
                ", nipam='" + pegawai.getNipam() + '\'' +
                ", nama='" + pegawai.getBiodata().getNama() + '\'' +
                ", jabatan= Jabatan( id=" + pegawai.getJabatan().getId() +
                ", nama='" + pegawai.getJabatan().getNama() + '\'' +
                ")) " +
                ", nipam='" + nipam + '\'' +
                ", nama='" + nama + '\'' +
                ", pangkatGolongan='" + pangkatGolongan + '\'' +
                ", organisasi=Organisasi( id=" + organisasi.getId() +
                "', nama='" + organisasi.getNama()
                + "')" +
                ", jabatan=Jabatan( id=" + jabatan.getId() + ", nama='" + jabatan.getNama() + "')" +
                ", jenisPengajuanCuti=" + jenisPengajuanCuti +
                ", refCuti=" + refCuti +
                ", jenisCuti=" + jenisCuti +
                ", subJenisCuti=" + subJenisCuti +
                ", tanggalMulai=" + tanggalMulai +
                ", tanggalSelesai=" + tanggalSelesai +
                ", jumlahHari=" + jumlahHari +
                ", jumlahHariKerja=" + jumlahHariKerja +
                ", kuotaAwal=" + kuotaAwal +
                ", kuotaAkhir=" + kuotaAkhir +
                ", alasan='" + alasan + '\'' +
                ", approvalCutiStatus=" + approvalCutiStatus +
                ", approvalLevel=" + approvalLevel +
                ", picSaatIni=" + picSaatIni +
                ", riwayatKuota0=" + riwayatKuota0 +
                ", riwayatKuota1=" + riwayatKuota1 +
                ", riwayatPakai0=" + riwayatPakai0 +
                ", riwayatPakai1=" + riwayatPakai1 +
                ", riwayatSisa0=" + riwayatSisa0 +
                ", riwayatSisa1=" + riwayatSisa1 +
                ", isClaimed=" + isClaimed +
                '}';
    }
}
