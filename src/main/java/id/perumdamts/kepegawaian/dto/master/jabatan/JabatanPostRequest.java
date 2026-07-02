package id.perumdamts.kepegawaian.dto.master.jabatan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@Data
public class JabatanPostRequest {
    @NotEmpty(message = "Kode is required")
    @NotNull(message = "Kode is required")
    private String kode;
    @Min(value = 1, message = "Jabatan Induk ID must be greater than or equal to 1")
    private Long parentId;
    @Min(value = 1, message = "Organisasi ID must be greater than or equal to 1")
    private Long organisasiId;
    @Min(value = 1, message = "Level ID must be greater than or equal to 1")
    private Long levelId;
    @NotEmpty(message = "Nama is required")
    private String nama;

    @JsonIgnore
    public Specification<Jabatan> getSpecification() {
        return SpecificationBuilder.<Jabatan>of()
                .addEqual(kode, "kode")
                .addEqual(parentId, "parent", "id")
                .addEqual(organisasiId, "organisasi", "id")
                .addEqual(levelId, "level", "id")
                .addEqual(nama, "nama")
                .build();
    }

}
