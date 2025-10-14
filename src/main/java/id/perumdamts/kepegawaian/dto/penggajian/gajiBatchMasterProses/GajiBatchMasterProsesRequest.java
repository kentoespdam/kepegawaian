package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiBatchMasterProsesRequest extends CommonPageRequest {
    private Long batchMasterId;
    @Enumerated(EnumType.STRING)
    private EJenisGaji jenisGaji;
    private String kode;

    @JsonIgnore
    public Specification<GajiBatchMasterProses> getSpecification() {
        return SpecificationBuilder.<GajiBatchMasterProses>of()
                .addEqual(batchMasterId, "batchMasterId")
                .addEqual(jenisGaji, "jenisGaji")
                .addEqual(kode, "kode")
                .build();
    }
}
