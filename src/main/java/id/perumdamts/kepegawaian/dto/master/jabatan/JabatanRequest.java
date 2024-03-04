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
    private Long organisasiId;
    private Long levelId;
    private String nama;
    private Long pangkatId;
    private Long golonganId;

    @JsonIgnore
    public Specification<Jabatan> getSpecification() {
        Specification<Jabatan> organisasiSpec = Objects.isNull(organisasiId) ? null : (root, query, cb) -> cb.equal(root.get("organisasi").get("id"), organisasiId);
        Specification<Jabatan> levelSpec = Objects.isNull(levelId) ? null : (root, query, cb) -> cb.equal(root.get("level").get("id"), organisasiId);
        Specification<Jabatan> namaSpec = Objects.isNull(nama) ? null : (root, query, cb) -> cb.like(root.get("nama"), "%" + nama + "%");
        Specification<Jabatan> pangkatSpec = Objects.isNull(pangkatId) ? null : (root, query, cb) -> cb.equal(root.get("pangkat").get("id"), pangkatId);
        Specification<Jabatan> golonganSpec = Objects.isNull(golonganId) ? null : (root, query, cb) -> cb.equal(root.get("golongan").get("id"), golonganId);

        return Specification.where(organisasiSpec).and(levelSpec).and(namaSpec).and(pangkatSpec).and(golonganSpec);
    }
}
