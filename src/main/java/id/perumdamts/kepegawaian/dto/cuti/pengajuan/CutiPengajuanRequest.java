package id.perumdamts.kepegawaian.dto.cuti.pengajuan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.commons.EJenisPengajuanCuti;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import jakarta.persistence.criteria.Expression;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Objects;

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
        Specification<CutiPegawai> idSpec = Objects.isNull(id) ? null :
                (root, query, cb) -> cb.equal(root.get("id"), id);
        Specification<CutiPegawai> pegawaiSpec = Objects.isNull(pegawaiId) ? null :
                (root, query, cb) -> cb.equal(root.get("pegawai").get("id"), pegawaiId);
        Specification<CutiPegawai> nipamSpec = Objects.isNull(nipam) ? null :
                (root, query, cb) -> cb.equal(root.get("pegawai").get("nipam"), nipam);
        Specification<CutiPegawai> namaSpec = Objects.isNull(nama) ? null :
                (root, query, cb) -> cb.like(root.get("pegawai").get("nama"), "%" + nama + "%");
        Specification<CutiPegawai> tahunSpec = Objects.isNull(tahun) ? null :
                (root, query, cb) -> {
                    Expression<LocalDate> createdAtPengajuanExpression = root.get("createdAt");
                    Expression<Integer> createdAtPengajuan = cb.function("YEAR", Integer.class, createdAtPengajuanExpression);
                    Expression<LocalDate> tanggalPengajuanExpression = root.get("tanggalMulai");
                    Expression<Integer> tahunPengajuan = cb.function("YEAR", Integer.class, tanggalPengajuanExpression);
                    return cb.or(
                            cb.equal(createdAtPengajuan, tahun),
                            cb.equal(tahunPengajuan, tahun)
                    );
                };
        Specification<CutiPegawai> jabatanSpec = Objects.isNull(jabatanId) ? null :
                (root, query, cb) -> cb.equal(root.get("jabatan").get("id"), jabatanId);
        Specification<CutiPegawai> picSaatIniSpec = Objects.isNull(picSaatIniId) ? null :
                (root, query, cb) -> {
                    Expression<Long> levelPic = root.get("picSaatIni").get("level").get("id");
                    return cb.or(
                            cb.equal(root.get("picSaatIni").get("id"), picSaatIniId),
                            cb.greaterThan(root.get("jabatan").get("level").get("id"), levelPic)
                    );
                };
        Specification<CutiPegawai> approvalCutiStatusSpec = Objects.isNull(approvalCutiStatus) ? null :
                (root, query, cb) -> cb.equal(root.get("approvalCutiStatus"), approvalCutiStatus);
        Specification<CutiPegawai> jenisPengajuanCutiSpec = Objects.isNull(jenisPengajuanCuti) ? null :
                (root, query, cb) -> cb.equal(root.get("jenisPengajuanCuti"), jenisPengajuanCuti);
        return Specification.where(idSpec).and(pegawaiSpec).and(nipamSpec).and(namaSpec)
                .and(tahunSpec).and(jabatanSpec).and(picSaatIniSpec)
                .and(approvalCutiStatusSpec).and(jenisPengajuanCutiSpec);
    }
}
