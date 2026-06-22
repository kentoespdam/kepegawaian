package id.perumdamts.kepegawaian.dto.master.sanksi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import id.perumdamts.kepegawaian.entities.master.Sanksi;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class SanksiIndexQuery extends PagedRequest {
    private String kode;
    private String keterangan;
    private Long jenisSpId;

    @JsonIgnore
    public Specification<Sanksi> getSpecification() {
        return SpecificationBuilder.<Sanksi>of()
                .addEqual(kode, "kode")
                .addLike(keterangan, "keterangan")
                .addEqual(jenisSpId, "jenisSp", "id")
                .build();
    }
}
