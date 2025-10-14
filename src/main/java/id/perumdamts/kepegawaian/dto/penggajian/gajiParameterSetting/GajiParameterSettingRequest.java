package id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.penggajian.GajiParameterSetting;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiParameterSettingRequest extends CommonPageRequest {
    private String kode;

    @JsonIgnore
    public Specification<GajiParameterSetting> getSpecification() {
        return SpecificationBuilder.<GajiParameterSetting>of()
                .addEqual(kode, "kode")
                .build();
    }
}
