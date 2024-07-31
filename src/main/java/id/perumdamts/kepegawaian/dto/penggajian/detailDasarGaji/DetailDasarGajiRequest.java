package id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.penggajian.DetailDasarGaji;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class DetailDasarGajiRequest extends CommonPageRequest {
    private Long dasarGajiId;
    private Integer mkg;
    private Long golonganId;
    private Integer nominal;

    @JsonIgnore
    public Specification<DetailDasarGaji> getSpecification() {
        Specification<DetailDasarGaji> dasarGajiSpec = Objects.isNull(dasarGajiId) ? null :
                (root, query, cb) -> cb.equal(root.get("dasarGaji").get("id"), dasarGajiId);
        Specification<DetailDasarGaji> mkgSpec = Objects.isNull(mkg) ? null :
                (root, query, cb) -> cb.equal(root.get("mkg"), mkg);
        Specification<DetailDasarGaji> golonganSpec = Objects.isNull(golonganId) ? null :
                (root, query, cb) -> cb.equal(root.get("golongan").get("id"), golonganId);
        Specification<DetailDasarGaji> nominalSpec = Objects.isNull(nominal) ? null :
                (root, query, cb) -> cb.equal(root.get("nominal"), nominal);
        return Specification.where(dasarGajiSpec).and(mkgSpec).and(golonganSpec).and(nominalSpec);
    }
}
