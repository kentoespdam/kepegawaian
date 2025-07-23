package id.perumdamts.kepegawaian.dto.cuti.pengajuan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EJenisPengajuanCuti;
import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
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
    public Specification<CutiPegawai> getSpecification() {
        Specification<CutiPegawai> yearSpec = (root, query, cb) -> {
            Expression<LocalDate> createdAtPengajuanExpression = root.get("createdAt");
            Expression<LocalDate> tanggalMulaiExpression = root.get("tanggalMulai");
            return cb.or(
                    cb.equal(cb.function("YEAR", Integer.class, createdAtPengajuanExpression), tahun),
                    cb.equal(cb.function("YEAR", Integer.class, tanggalMulaiExpression), tahun)
            );
        };
        Specification<CutiPegawai> approvalSpec = (root, query, cb) -> cb.equal(root.get("approvalCutiStatus"), approvalCutiStatus);
        Specification<CutiPegawai> picSaatIniSpec = (root, query, cb) -> cb.equal(root.get("picSaatIni").get("id"), picSaatIniId);
        Specification<CutiPegawai> jenisPengajuanCutiSpec = Objects.isNull(jenisPengajuanCuti) ? null :
                (root, query, cb) -> cb.equal(root.get("jenisPengajuanCuti"), jenisPengajuanCuti);
        return Specification.where(yearSpec).and(approvalSpec).and(picSaatIniSpec).and(jenisPengajuanCutiSpec);
    }

    @JsonIgnore
    public Specification<CutiApprovalChain> getApprovalChainSpecification() {
        Specification<CutiApprovalChain> picSaatIniSpec =
                (root, query, cb) -> cb.equal(root.get("jabatanId"), picSaatIniId);
        Specification<CutiApprovalChain> tahunSpec = (root, query, cb) -> {
            Expression<LocalDate> createdAtPengajuanExpression = root.get("refCuti").get("createdAt");
            Expression<LocalDate> tanggalMulaiExpression = root.get("refCuti").get("tanggalMulai");
            return cb.or(
                    cb.equal(cb.function("YEAR", Integer.class, createdAtPengajuanExpression), tahun),
                    cb.equal(cb.function("YEAR", Integer.class, tanggalMulaiExpression), tahun)
            );
        };
        Specification<CutiApprovalChain> approvalSpec = (root, query, cb) ->
                cb.equal(root.get("refCuti").get("approvalCutiStatus"), approvalCutiStatus);
        Specification<CutiApprovalChain> jenisPengajuanCutiSpec = Objects.isNull(jenisPengajuanCuti) ? null :
                (root, query, cb) -> cb.equal(root.get("refCuti").get("jenisPengajuanCuti"), jenisPengajuanCuti);
        Specification<CutiApprovalChain> levelSpec = (root, query, cb) -> cb.or(
                cb.equal(root.get("refCuti").get("approvalLevel"), root.get("approvalLevel")),
                cb.equal(root.get("skip"), approvalCutiStatus.equals(EApprovalCutiStatus.PENDING))
        );
        return Specification.where(picSaatIniSpec).and(tahunSpec).and(approvalSpec).and(jenisPengajuanCutiSpec).and(levelSpec);
    }
}
