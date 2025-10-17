package id.perumdamts.kepegawaian.dto.cuti.approvalChain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EReadWriteStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class CutiApprovalChainRequest extends CommonPageRequest {
    @NotNull(message = "Tahun is required")
    @Min(value = 2000, message = "Tahun is required")
    private Integer tahun;
    @NotNull(message = "PIC Saat Ini is required")
    @Min(value = 1, message = "PIC Saat Ini is required")
    private Long picSaatIniId;
    @NotNull(message = "Approval Cuti Status is required")
    private EApprovalCutiStatus approvalCutiStatus;
    private Long jabatanId;
    private EReadWriteStatus readWriteStatus;

    @JsonIgnore
    public Specification<CutiApprovalChain> getApprovalChainSpecification() {
        return SpecificationBuilder.<CutiApprovalChain>of()
                .addEqual(jabatanId, "jabatanId")
                .addEqual(picSaatIniId, "jabatanId")
                .addEqual(approvalCutiStatus, "refCuti", "approvalCutiStatus")
                .addEqual(readWriteStatus, "readWriteStatus")
                .addCustom((root, cb) -> createYearPredicate(root, cb, tahun))
                .build();
    }

    private Predicate createYearPredicate(Root<CutiApprovalChain> root, CriteriaBuilder cb, Integer tahun) {
        if (tahun == null) return null;

        Expression<LocalDate> createdAtPengajuanExpression = root.get("refCuti").get("createdAt");
        Expression<LocalDate> tanggalMulaiExpression = root.get("refCuti").get("tanggalMulai");

        return cb.or(
                cb.equal(cb.function("YEAR", Integer.class, createdAtPengajuanExpression), tahun),
                cb.equal(cb.function("YEAR", Integer.class, tanggalMulaiExpression), tahun)
        );
    }

}