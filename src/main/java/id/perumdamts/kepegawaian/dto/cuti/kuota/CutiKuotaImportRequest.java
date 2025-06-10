package id.perumdamts.kepegawaian.dto.cuti.kuota;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.cuti.CutiKuota;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CutiKuotaImportRequest {
    @NotNull(message = "Tahun is required")
    @Min(value = 2000, message = "Tahun is required")
    private Integer tahun;
    @NotNull(message = "File is required")
    private MultipartFile file;

    @JsonIgnore
    public Specification<CutiKuota> getSpecification() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("tahun"), tahun);
    }
}
