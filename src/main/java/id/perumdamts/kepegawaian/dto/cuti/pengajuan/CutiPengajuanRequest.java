package id.perumdamts.kepegawaian.dto.cuti.pengajuan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

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

    @JsonIgnore
    public Specification<CutiPegawai> getSpecification() {
        Specification<CutiPegawai> idSpec = Objects.isNull(id) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), id);
        Specification<CutiPegawai> pegawaiSpec = Objects.isNull(pegawaiId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("pegawai").get("id"), pegawaiId);
        Specification<CutiPegawai> nipamSpec = Objects.isNull(nipam) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("pegawai").get("nipam"), nipam);
        Specification<CutiPegawai> namaSpec = Objects.isNull(nama) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("pegawai").get("nama"), "%" + nama + "%");
        Specification<CutiPegawai> tahunSpec = Objects.isNull(tahun) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("tahun"), tahun);
        Specification<CutiPegawai> jabatanSpec = Objects.isNull(jabatanId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("jabatan").get("id"), jabatanId);
        Specification<CutiPegawai> picSaatIniSpec = Objects.isNull(picSaatIniId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("picSaatIni").get("id"), picSaatIniId);
        return Specification.where(idSpec).and(pegawaiSpec).and(nipamSpec).and(namaSpec).and(tahunSpec).and(jabatanSpec).and(picSaatIniSpec);
    }
}
