package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiBatchMasterRequest extends CommonPageRequest {
    @NotEmpty(message = "is Required")
    private String periode;
    @NotNull(message = "is Required")
    private String status;
    private String nipam;
    private String nama;
    private Long pegawaiId;

    @JsonIgnore
    public Specification<GajiBatchMaster> getSpecification() {
        SpecificationBuilder<GajiBatchMaster> builder = SpecificationBuilder.<GajiBatchMaster>of()
                .addEqual(periode, "gajiBatchRoot", "periode")
                .addEqual(status, "gajiBatchRoot", "status")
                .addEqual(nipam, "nipam")
                .addLike(nama, "nama")
                .addEqual(pegawaiId, "pegawai", "id");
        return builder.build();
    }
}
