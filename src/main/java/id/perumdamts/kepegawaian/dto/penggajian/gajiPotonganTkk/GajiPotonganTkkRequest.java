package id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPotonganTkk;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiPotonganTkkRequest extends CommonPageRequest {
    private EStatusPegawai statusPegawai;
    private Long levelId;
    private Long golonganId;

    public Specification<GajiPotonganTkk> getSpecification() {
        Specification<GajiPotonganTkk> statusSpec = Objects.isNull(statusPegawai) ? null :
                (root, query, cb) -> cb.equal(root.get("statusPegawai"), statusPegawai);
        Specification<GajiPotonganTkk> levelSpec = Objects.isNull(levelId) ? null :
                (root, query, cb) -> cb.equal(root.get("level").get("id"), levelId);
        Specification<GajiPotonganTkk> golonganSpec = Objects.isNull(golonganId) ? null :
                (root, query, cb) -> cb.equal(root.get("golongan").get("id"), golonganId);
        return Specification.where(statusSpec).and(levelSpec).and(golonganSpec);
    }
}
