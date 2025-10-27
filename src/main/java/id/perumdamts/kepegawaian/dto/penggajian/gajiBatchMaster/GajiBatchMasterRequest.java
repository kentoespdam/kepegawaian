package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiBatchMasterRequest extends CommonPageRequest {
    @NotEmpty(message = "is Required")
    private String periode;
    @NotNull(message = "is Required")
    @Enumerated(EnumType.STRING)
    private EProsesGaji status;
    private String nipam;
    private String nama;
    private Long pegawaiId;

    @JsonIgnore
    public Specification<GajiBatchMaster> getSpecification() {
        SpecificationBuilder<GajiBatchMaster> builder = SpecificationBuilder.<GajiBatchMaster>of()
                .addEqual(periode, "gajiBatchRoot", "periode")
                .addEqual(nipam, "nipam")
                .addLike(nama, "nama")
                .addEqual(pegawaiId, "pegawai", "id");
        if (Objects.nonNull(status))
            builder.addGreaterThanOrEqual(status.ordinal(), "gajiBatchRoot", "status");
        return builder.build();
    }
}
