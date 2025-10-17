package id.perumdamts.kepegawaian.dto.cuti.pengajuan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EJenisPengajuanCuti;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class CutiPengajuanRequest extends CommonPageRequest {
    private Long id;
    private Long pegawaiId;
    private String nipam;
    private String nama;
    private Integer tahun;
    private Long jabatanId;
    private Long picSaatIniId;
    private EApprovalCutiStatus approvalCutiStatus;
    private EJenisPengajuanCuti jenisPengajuanCuti;

    @JsonIgnore
    public Specification<CutiPegawai> getSpecification() {
        return SpecificationBuilder.<CutiPegawai>of()
                .addEqual(id, "id")
                .addEqual(pegawaiId, "pegawai", "id")
                .addLike(nipam, "pegawai", "nipam")
                .addLike(nama, "pegawai", "biodata", "nama")
                .addCustom((root, cb) -> createTahunPredicate(root, cb, tahun))
                .addEqual(jabatanId, "jabatan", "id")
                .addEqual(picSaatIniId, "picSaatIni", "id")
                .addEqual(approvalCutiStatus, "approvalCutiStatus")
                .addEqual(jenisPengajuanCuti, "jenisPengajuanCuti")
                .build();
    }

    private Predicate createTahunPredicate(Root<CutiPegawai> root, CriteriaBuilder cb, Integer tahun) {
        if (tahun == null) return null;

        Path<LocalDate> createdAtExpression = root.get("createdAt");
        Path<LocalDate> tanggalMulaiExpression = root.get("tanggalMulai");
        return cb.or(
                cb.equal(cb.function("YEAR", Integer.class, createdAtExpression), tahun),
                cb.equal(cb.function("MONTH", Integer.class, tanggalMulaiExpression), tahun)
        );
    }
}
