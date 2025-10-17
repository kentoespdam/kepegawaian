package id.perumdamts.kepegawaian.dto.cuti.approval;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiApproval;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class CutiApprovalRequest extends CommonPageRequest {
    private Long id;
    private Long cutiId;
    private Long approverId;
    private Long jabatanId;

    @JsonIgnore
    public Specification<CutiApproval> getSpecification() {
        return SpecificationBuilder.<CutiApproval>of()
                .addEqual(id, "id")
                .addEqual(cutiId, "cutiPegawai", "id")
                .addEqual(approverId, "approver", "id")
                .addEqual(jabatanId, "jabatan", "id")
                .build();
    }
}
