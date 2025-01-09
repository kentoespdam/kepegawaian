package id.perumdamts.kepegawaian.dto.master.jabatan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class JabatanRequest extends CommonPageRequest {
    private String kode;
    private Long parentId;
    private Long organisasiId;
    private Long levelId;
    private String nama;

    @JsonIgnore
    public Specification<Jabatan> getSpecification() {
        Specification<Jabatan> kodeSpec = Objects.isNull(kode) ? null :
                (root, query, cb) -> cb.equal(root.get("kode"), kode);
        Specification<Jabatan> parentSpec = Objects.isNull(parentId) ? null :
                (root, query, cb) -> cb.equal(root.get("parent").get("id"), parentId);
        Specification<Jabatan> organisasiSpec = Objects.isNull(organisasiId) ? null :
                (root, query, cb) -> cb.equal(root.get("organisasi").get("id"), organisasiId);
        Specification<Jabatan> levelSpec = Objects.isNull(levelId) ? null :
                (root, query, cb) -> cb.equal(root.get("level").get("id"), levelId);
        Specification<Jabatan> namaSpec = Objects.isNull(nama) ? null :
                (root, query, cb) -> cb.like(root.get("nama"), "%" + nama + "%");

        return Specification.where(kodeSpec).and(parentSpec).and(organisasiSpec).and(levelSpec)
                .and(namaSpec);
    }
}
