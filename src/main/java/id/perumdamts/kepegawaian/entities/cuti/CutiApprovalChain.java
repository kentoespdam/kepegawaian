package id.perumdamts.kepegawaian.entities.cuti;

import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EReadWriteStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(indexes = {
        @Index(columnList = "readWriteStatus"),
        @Index(columnList = "approvalStatus"),
})
@Getter
@Setter
@NoArgsConstructor
public class CutiApprovalChain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "ref_cuti_id", referencedColumnName = "id")
    private CutiPegawai refCuti;
    private Long jabatanId;
    private String jabatanNama;
    private Integer approvalLevel;
    @Enumerated(EnumType.ORDINAL)
    private EApprovalCutiStatus approvalStatus;
    @Enumerated(EnumType.ORDINAL)
    private EReadWriteStatus readWriteStatus;

    public CutiApprovalChain(Long id, CutiPegawai refCuti, Long jabatanId, String jabatanNama, Integer approvalLevel, EApprovalCutiStatus approvalStatus, EReadWriteStatus readWriteStatus) {
        this.id = id;
        this.refCuti = refCuti;
        this.jabatanId = jabatanId;
        this.jabatanNama = jabatanNama;
        this.approvalLevel = approvalLevel;
        this.approvalStatus = approvalStatus;
        this.readWriteStatus = readWriteStatus;
    }

    public CutiApprovalChain(CutiPegawai refCutiId, Long jabatanId, String jabatanNama, int approvalLevel) {
        this.refCuti = refCutiId;
        this.jabatanId = jabatanId;
        this.jabatanNama = jabatanNama;
        this.approvalLevel = approvalLevel;
        this.approvalStatus = EApprovalCutiStatus.PENDING;
        this.readWriteStatus = EReadWriteStatus.NONE;
    }

    public CutiApprovalChain(CutiPegawai refCutiId, Long jabatanId, String jabatanNama, int approvalLevel, EApprovalCutiStatus approvalStatus, EReadWriteStatus readWrite) {
        this.refCuti = refCutiId;
        this.jabatanId = jabatanId;
        this.jabatanNama = jabatanNama;
        this.approvalLevel = approvalLevel;
        this.approvalStatus = approvalStatus;
        this.readWriteStatus = readWrite;
    }

    @Override
    public String toString() {
        return "CutiApprovalChain{" +
                "id=" + id +
                ", refCuti=" + refCuti.getId() +
                ", jabatanId=" + jabatanId +
                ", jabatanNama='" + jabatanNama + '\'' +
                ", approvalLevel=" + approvalLevel +
                ", approvalStatus=" + approvalStatus +
                ", readWriteStatus=" + readWriteStatus +
                '}';
    }
}
