package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiBatchMasterProsesRequest extends CommonPageRequest {
    private Long batchMasterId;
    @Enumerated(EnumType.STRING)
    private EJenisGaji jenisGaji;
    private String kode;

    @JsonIgnore
    public Specification<GajiBatchMasterProses> getSpecification() {
        Specification<GajiBatchMasterProses> batchMasterIdSpec = Objects.isNull(batchMasterId) ? null :
                (root, query, cb) -> cb.equal(root.get("batchMasterId"), batchMasterId);
        Specification<GajiBatchMasterProses> jenisGajiSpec = Objects.isNull(jenisGaji) ? null :
                (root, query, cb) -> cb.equal(root.get("jenisGaji"), jenisGaji);
        Specification<GajiBatchMasterProses> kodeSpec = Objects.isNull(kode) ? null :
                (root, query, cb) -> cb.equal(root.get("kode"), kode);

        return Specification.where(batchMasterIdSpec).and(jenisGajiSpec).and(kodeSpec);
    }
}
