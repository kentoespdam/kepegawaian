package id.perumdamts.kepegawaian.dto.master.alatKerja;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.AlatKerja;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class AlatKerjaPostRequest {
    @NotNull(message = "Profesi ID is required")
    @Min(value = 1, message = "Profesi ID must be greater than or equal to 1")
    private Long profesiId;
    @NotEmpty(message = "Nama is required")
    private String nama;

    @JsonIgnore
    public Specification<AlatKerja> getSpecification() {
        return SpecificationBuilder.<AlatKerja>of()
                .addEqual(nama, "nama")
                .addEqual(profesiId, "profesi", "id")
                .build();
    }

    public static AlatKerja toEntity(AlatKerjaPostRequest request, Profesi profesi) {
        AlatKerja entity = new AlatKerja();
        entity.setProfesi(profesi);
        entity.setNama(request.getNama());
        return entity;
    }
}
