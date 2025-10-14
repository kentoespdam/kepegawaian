package id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.penggajian.GajiParameterSetting;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class GajiParameterSettingPostRequest {
    @NotEmpty(message = "Kode is required")
    private String kode;
    @NotNull(message = "Nominal is required")
    private Double nominal;

    @JsonIgnore
    public Specification<GajiParameterSetting> getSpecification() {
        return SpecificationBuilder.<GajiParameterSetting>of()
                .addEqual(kode, "kode")
                .build();
    }

    public static GajiParameterSetting toEntity(GajiParameterSettingPostRequest request) {
        GajiParameterSetting entity = new GajiParameterSetting();
        entity.setKode(request.getKode());
        entity.setNominal(request.getNominal());
        return entity;
    }
}
