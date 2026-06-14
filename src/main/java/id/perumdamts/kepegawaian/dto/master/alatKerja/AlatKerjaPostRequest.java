package id.perumdamts.kepegawaian.dto.master.alatKerja;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.AlatKerja;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class AlatKerjaPostRequest {
    @NotEmpty(message = "Nama is required")
    private String nama;

    @JsonIgnore
    public Specification<AlatKerja> getSpecification() {
        return SpecificationBuilder.<AlatKerja>of()
                .addEqual(nama, "nama")
                .build();
    }

    public static AlatKerja toEntity(AlatKerjaPostRequest request, Profesi profesi) {
        AlatKerja entity = new AlatKerja();
        entity.setProfesi(profesi);
        entity.setNama(request.getNama());
        return entity;
    }
}
