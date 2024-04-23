package id.perumdamts.kepegawaian.dto.profil.keahlian;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EKualifikasi;
import id.perumdamts.kepegawaian.entities.profil.Keahlian;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class KeahlianRequest extends CommonPageRequest {
    private String biodataId;
    private String keahlianId;
    @Enumerated(EnumType.ORDINAL)
    private EKualifikasi kualifikasi;
    private Boolean sertifikasi;
    private String institusi;
    private Integer tahun;

    public Specification<Keahlian> getSpecification() {
        Specification<Keahlian> biodataSpec = Objects.isNull(biodataId) ? null :
                (root, query, cb) -> cb.equal(root.get("biodata").get("nik"), biodataId);
        Specification<Keahlian> keahlianSpec = Objects.isNull(keahlianId) ? null :
                (root, query, cb) -> cb.equal(root.get("keahlian").get("id"), keahlianId);
        Specification<Keahlian> kualifikasiSpec = Objects.isNull(kualifikasi) ? null :
                (root, query, cb) -> cb.equal(root.get("kualifikasi"), kualifikasi);
        Specification<Keahlian> sertifikasiSpec = Objects.isNull(sertifikasi) ? null :
                (root, query, cb) -> cb.equal(root.get("sertifikasi"), sertifikasi);
        Specification<Keahlian> institusiSpec = Objects.isNull(institusi) ? null :
                (root, query, cb) -> cb.like(root.get("institusi"), "%" + institusi + "%");
        Specification<Keahlian> tahunSpec = Objects.isNull(tahun) ? null :
                (root, query, cb) -> cb.equal(root.get("tahun"), tahun);
        return Specification.where(biodataSpec).and(keahlianSpec)
                .and(kualifikasiSpec).and(sertifikasiSpec).and(institusiSpec).and(tahunSpec);
    }
}
