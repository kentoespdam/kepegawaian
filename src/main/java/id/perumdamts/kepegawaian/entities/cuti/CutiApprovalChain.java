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
    private Long refCutiId;
    private Long jabatanId;
    private String jabatanNama;
    private Integer approvalLevel;

    public CutiApprovalChain(Long refCutiId, Long jabatanId, String jabatanNama, int approvalLevel) {
        this.refCutiId = refCutiId;
        this.jabatanId = jabatanId;
        this.jabatanNama = jabatanNama;
        this.approvalLevel = approvalLevel;
    }
}
