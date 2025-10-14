package id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSp;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class RiwayatSpRequest extends CommonPageRequest {
    private Long pegawaiId;
    private String nomorSp;
    private Long jenisSpId;

    @JsonIgnore
    public Specification<RiwayatSp> getSpecification() {
        return SpecificationBuilder.<RiwayatSp>of()
                .addEqual(nomorSp, "nomorSp")
                .addEqual(pegawaiId, "pegawai", "id")
                .addEqual(jenisSpId, "jenisSp", "id")
                .build();
    }
}
