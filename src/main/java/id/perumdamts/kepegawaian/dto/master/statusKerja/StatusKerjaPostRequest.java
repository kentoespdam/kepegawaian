package id.perumdamts.kepegawaian.dto.master.statusKerja;

import id.perumdamts.kepegawaian.entities.master.StatusKerja;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;

@Data
public class StatusKerjaPostRequest {
    @NotEmpty(message = "Nama is required")
    private String nama;

    public Specification<StatusKerja> getSpecification() {
        Specification<StatusKerja> namaSpec = Objects.isNull(nama) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("nama"), nama);

        return Specification.where(namaSpec);
    }

    public static StatusKerja toEntity(StatusKerjaPostRequest request) {
        return new StatusKerja(null, request.getNama());
    }

    public static List<StatusKerja> toEntities(List<StatusKerjaPostRequest> requests) {
        return requests.stream().map(StatusKerjaPostRequest::toEntity).toList();
    }
}
