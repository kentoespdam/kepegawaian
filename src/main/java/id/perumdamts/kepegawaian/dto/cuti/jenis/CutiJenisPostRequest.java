package id.perumdamts.kepegawaian.dto.cuti.jenis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import org.jspecify.annotations.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class CutiJenisPostRequest {
    private Long parentId;
    @NotNull(message = "Nama is required")
    @NotEmpty(message = "Nama is required")
    private String nama;
    private Integer maxHari = 0;
    private Boolean potongKuotaTahunan = false;

    @JsonIgnore
    public Specification<CutiJenis> getSpecification() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(criteriaBuilder.equal(criteriaBuilder.lower(root.get("nama")), nama.toLowerCase()));
    }

    public static CutiJenis toEntity(CutiJenisPostRequest request, @Nullable CutiJenis parent) {
        CutiJenis cutiJenis = new CutiJenis();
        cutiJenis.setParent(parent);
        cutiJenis.setNama(request.getNama());
        cutiJenis.setMaxHari(request.getMaxHari());
        cutiJenis.setPotongKuotaTahunan(request.getPotongKuotaTahunan());
        return cutiJenis;
    }
}
