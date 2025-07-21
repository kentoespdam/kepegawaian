package id.perumdamts.kepegawaian.dto.cuti.pengajuan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EJenisPengajuanCuti;
import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;
import jakarta.persistence.criteria.Expression;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class CutiPengajuanApprovalRequest extends CommonPageRequest {
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
    public Specification<CutiApprovalChain> getSpecification(long supervisorSdmId) {
        Specification<CutiApprovalChain> yearSpec = (root, query, cb) -> {
            Expression<Integer> yearCreatedAt = cb.function("YEAR", Integer.class, root.get("refCuti").get("createdAt"));
            Expression<Integer> yearTanggalMulai = cb.function("YEAR", Integer.class, root.get("refCuti").get("tanggalMulai"));
            return cb.or(
                    cb.equal(yearCreatedAt, tahun),
                    cb.equal(yearTanggalMulai, tahun)
            );
        };

        Specification<CutiApprovalChain> approvalLevelSpec = (root, query, cb) -> {
            Expression<Integer> approvalLevel = root.get("refCuti").get("approvalLevel");
            return cb.greaterThanOrEqualTo(root.get("approvalLevel"), approvalLevel);
        };

        Specification<CutiApprovalChain> picSaatIniSpec = (picSaatIniId.equals(supervisorSdmId)) ?
                (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("jabatanId"), picSaatIniId) :
                (root, query, cb) -> cb.equal(root.get("jabatanId"), picSaatIniId);

        Specification<CutiApprovalChain> jenisPengajuanCutiSpec = (root, query, cb) ->
                cb.equal(root.get("refCuti").get("jenisPengajuanCuti"), Objects.isNull(jenisPengajuanCuti) ? EJenisPengajuanCuti.PENGAJUAN_CUTI : jenisPengajuanCuti);

        return Specification.where(yearSpec)
                .and(approvalLevelSpec)
                .and(picSaatIniSpec)
                .and(jenisPengajuanCutiSpec);
    }
}
