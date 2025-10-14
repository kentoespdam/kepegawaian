package id.perumdamts.kepegawaian.dto.penggajian.gajiProfil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class GajiProfilPostRequest {
    @NotEmpty(message = "Nama is required")
    @NotNull(message = "Nama is required")
    private String nama;

    @JsonIgnore
    public Specification<GajiProfil> getSpecification() {
        return SpecificationBuilder.<GajiProfil>of()
                .addEqual(nama, "nama")
                .build();
    }

    public static GajiProfil toEntity(GajiProfilPostRequest request) {
        GajiProfil entity = new GajiProfil();
        entity.setNama(request.getNama());
        return entity;
    }
}
