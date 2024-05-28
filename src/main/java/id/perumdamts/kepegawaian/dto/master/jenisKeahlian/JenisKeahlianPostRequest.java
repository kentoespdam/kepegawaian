package id.perumdamts.kepegawaian.dto.master.jenisKeahlian;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.JenisKeahlian;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;

@Data
public class JenisKeahlianPostRequest {
    @NotEmpty(message = "Nama is required")
    private String nama;

    @JsonIgnore
    public Specification<JenisKeahlian> getSpecification() {
        Specification<JenisKeahlian> namaSpec = Objects.isNull(nama) ? null : (root, query, cb) -> cb.equal(root.get("nama"), nama);
        return Specification.where(namaSpec);
    }

    public static JenisKeahlian toEntity(JenisKeahlianPostRequest request) {
        return new JenisKeahlian(request.getNama());
    }

    public static JenisKeahlian toEntity(JenisKeahlianPostRequest request, JenisKeahlian entity) {
        entity.setNama(request.getNama());
        return entity;
    }

    public static List<JenisKeahlian> toEntities(List<JenisKeahlianPostRequest> requests) {
        return requests.stream().map(JenisKeahlianPostRequest::toEntity).toList();
    }
}
