package id.perumdamts.kepegawaian.entities.cuti;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    public CutiApprovalChain(CutiPegawai refCutiId, Long jabatanId, String jabatanNama, int approvalLevel) {
        this.refCuti = refCutiId;
        this.jabatanId = jabatanId;
        this.jabatanNama = jabatanNama;
        this.approvalLevel = approvalLevel;
    }
}
