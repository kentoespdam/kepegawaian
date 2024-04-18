package id.perumdamts.kepegawaian.dto.master.jenisKitas;

import id.perumdamts.kepegawaian.entities.master.JenisKitas;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;

@Data
public class JenisKitasPostRequest {
    @NotEmpty(message = "Nama is required")
    private String nama;

    public static JenisKitas toEntity(JenisKitasPostRequest request) {
        return new JenisKitas(request.getNama());
    }

    public static JenisKitas toEntity(JenisKitasPostRequest request, JenisKitas entity) {
        entity.setNama(request.getNama());
        return entity;
    }

    public static List<JenisKitas> toEntities(List<JenisKitasPostRequest> requests) {
        return requests.stream().map(JenisKitasPostRequest::toEntity).toList();
    }

    public Specification<JenisKitas> getSpecification() {
        Specification<JenisKitas> namaSpec = Objects.isNull(nama) ? null : (root, query, cb) -> cb.equal(root.get("nama"), nama);
        return Specification.where(namaSpec);
    }
}
