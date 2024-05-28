package id.perumdamts.kepegawaian.dto.master.jenisPelatihan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.JenisPelatihan;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;

@Data
public class JenisPelatihanPostRequest {
    @NotEmpty(message = "Nama is required")
    private String nama;

    @JsonIgnore
    public Specification<JenisPelatihan> getSpecification() {
        Specification<JenisPelatihan> namaSpec = Objects.isNull(nama) ? null : (root, query, cb) -> cb.equal(root.get("nama"), nama);
        return Specification.where(namaSpec);
    }

    public static JenisPelatihan toEntity(JenisPelatihanPostRequest request) {
        return new JenisPelatihan(request.getNama());
    }

    public static JenisPelatihan toEntity(JenisPelatihanPostRequest request, JenisPelatihan entity) {
        entity.setNama(request.getNama());
        return entity;
    }

    public static List<JenisPelatihan> toEntities(List<JenisPelatihanPostRequest> requests) {
        return requests.stream().map(JenisPelatihanPostRequest::toEntity).toList();
    }
}
