package id.perumdamts.kepegawaian.dto.cuti.approvalChain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EJenisPengajuanCuti;
import id.perumdamts.kepegawaian.entities.commons.EReadWriteStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;
import jakarta.persistence.criteria.Expression;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Objects;

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
    private EJenisPengajuanCuti jenisPengajuanCuti;

    @JsonIgnore
    public Specification<CutiApprovalChain> getApprovalChainSpecification() {
        Specification<CutiApprovalChain> picSaatIniSpec =
                (root, query, cb) ->
                        cb.equal(root.get("jabatanId"), picSaatIniId);
        Specification<CutiApprovalChain> tahunSpec =
                (root, query, cb) -> {
                    Expression<LocalDate> createdAtPengajuanExpression = root.get("refCuti").get("createdAt");
                    Expression<LocalDate> tanggalMulaiExpression = root.get("refCuti").get("tanggalMulai");
                    return cb.or(
                            cb.equal(cb.function("YEAR", Integer.class, createdAtPengajuanExpression), tahun),
                            cb.equal(cb.function("YEAR", Integer.class, tanggalMulaiExpression), tahun)
                    );
                };
        Specification<CutiApprovalChain> jenisPengajuanCutiSpec = Objects.isNull(jenisPengajuanCuti) ? null :
                (root, query, cb) ->
                        cb.equal(root.get("refCuti").get("jenisPengajuanCuti"), jenisPengajuanCuti);
        Specification<CutiApprovalChain> readWriteSpec = (root, query, cb) ->
                cb.notEqual(root.get("readWriteStatus"), EReadWriteStatus.NONE);
        Specification<CutiApprovalChain> approvalCutiSpec = (root, query, cb) ->
                cb.equal(root.get("refCuti").get("approvalCutiStatus"), approvalCutiStatus);
//        Specification<CutiApprovalChain> approvalChainSpec = (root, query, cb) ->
//                cb.equal(root.get("approvalStatus"), approvalCutiStatus);

        return Specification.where(picSaatIniSpec).and(tahunSpec)
                .and(approvalCutiSpec)
                .and(jenisPengajuanCutiSpec).and(readWriteSpec);
//                .or(approvalChainSpec);
    }
}
