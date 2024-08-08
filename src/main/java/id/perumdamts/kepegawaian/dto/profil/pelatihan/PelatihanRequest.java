package id.perumdamts.kepegawaian.dto.profil.pelatihan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.profil.Pelatihan;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class PelatihanRequest extends CommonPageRequest {
    private String biodataId;
    private String jenisPelatihanId;
    private String nama;
    private Boolean lulus;
    private Boolean disetujui;

    @JsonIgnore
    public Specification<Pelatihan> getSpecification() {
        Specification<Pelatihan> biodataSpec = Objects.isNull(biodataId) ? null :
                (root, query, cb) -> cb.equal(root.get("biodata").get("nik"), biodataId);
        Specification<Pelatihan> jenisPelatihanSpec = Objects.isNull(jenisPelatihanId) ? null :
                (root, query, cb) -> cb.equal(root.get("jenisPelatihan").get("id"), jenisPelatihanId);
        Specification<Pelatihan> namaSpec = Objects.isNull(nama) ? null :
                (root, query, cb) -> cb.like(root.get("nama"), "%" + nama + "%");
        Specification<Pelatihan> lulusSpec = Objects.isNull(lulus) ? null :
                (root, query, cb) -> cb.equal(root.get("lulus"), lulus);
        Specification<Pelatihan> disetujuiSpec = Objects.isNull(disetujui) ? null :
                (root, query, cb) -> cb.equal(root.get("disetujui"), disetujui);
        return Specification.where(biodataSpec).and(jenisPelatihanSpec)
                .and(namaSpec).and(lulusSpec).and(disetujuiSpec);
    }
}
