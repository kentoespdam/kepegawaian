package id.perumdamts.kepegawaian.dto.profil.pendidikan;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class PendidikanRequest extends CommonPageRequest {
    private String biodataId;
    private Long jenjangId;
    private String gelarDepan;
    private String gelarBelakang;
    private String jurusan;
    private String institusi;
    private Integer tahunMasuk;
    private Integer tahunLulus;
    private Double gpa;
    private Boolean isLatest;
    private Boolean disetujui;

    public Specification<Pendidikan> getSpecification() {
        Specification<Pendidikan> biodataSpec = Objects.isNull(biodataId) ? null :
                (root, query, cb) -> cb.equal(root.get("biodata").get("nik"), biodataId);
        Specification<Pendidikan> jenjangSpec = Objects.isNull(jenjangId) ? null :
                (root, query, cb) -> cb.equal(root.get("jenjangPendidikan").get("id"), jenjangId);
        Specification<Pendidikan> gelarSpec = Objects.isNull(gelarDepan) ? null :
                (root, query, cb) -> cb.equal(root.get("gelarDepan"), gelarDepan);
        Specification<Pendidikan> jurusanSpec = Objects.isNull(jurusan) ? null :
                (root, query, cb) -> cb.like(root.get("jurusan"), "%" + jurusan + "%");
        Specification<Pendidikan> institusiSpec = Objects.isNull(institusi) ? null :
                (root, query, cb) -> cb.like(root.get("institusi"), "%" + institusi + "%");
        Specification<Pendidikan> tahunMasukSpec = Objects.isNull(tahunMasuk) ? null :
                (root, query, cb) -> cb.equal(root.get("tahunMasuk"), tahunMasuk);
        Specification<Pendidikan> tahunLulusSpec = Objects.isNull(tahunLulus) ? null :
                (root, query, cb) -> cb.equal(root.get("tahunLulus"), tahunLulus);
        Specification<Pendidikan> gpaSpec = Objects.isNull(gpa) ? null :
                (root, query, cb) -> cb.equal(root.get("gpa"), gpa);
        Specification<Pendidikan> isLatestSpec = Objects.isNull(isLatest) ? null :
                (root, query, cb) -> cb.equal(root.get("isLatest"), isLatest);
        Specification<Pendidikan> disetujuiSpec = Objects.isNull(disetujui) ? null :
                (root, query, cb) -> cb.equal(root.get("disetujui"), disetujui);
        return Specification.where(biodataSpec).and(jenjangSpec).and(gelarSpec)
                .and(jurusanSpec).and(institusiSpec).and(tahunMasukSpec)
                .and(tahunLulusSpec).and(gpaSpec).and(isLatestSpec).and(disetujuiSpec);
    }
}
