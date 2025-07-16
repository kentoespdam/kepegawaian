package id.perumdamts.kepegawaian.dto.cuti.approval;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiApproval;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class CutiApprovalRequest extends CommonPageRequest {
    private Long id;
    private Long cutiId;
    private Long approverId;
    private Long jabatanId;

    @JsonIgnore
    public Specification<CutiApproval> getSpecification() {
        Specification<CutiApproval> idSpec = Objects.isNull(id) ? null :
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("id"), id);
        Specification<CutiApproval> cutiIdSpec = Objects.isNull(cutiId) ? null :
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("cutiPegawai").get("id"), cutiId);
        Specification<CutiApproval> approverIdSpec = Objects.isNull(approverId) ? null :
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("approver").get("id"), approverId);
        Specification<CutiApproval> jabatanIdSpec = Objects.isNull(jabatanId) ? null :
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("jabatan").get("id"), jabatanId);

        return Specification.where(idSpec).and(cutiIdSpec).and(approverIdSpec).and(jabatanIdSpec);
    }
}
