package id.perumdamts.kepegawaian.dto.master.jenisSp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.JenisSp;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@Data
public class JenisSpPostRequest {
    @NotEmpty(message = "Nama is required")
    private String nama;
    private String notes;

    @JsonIgnore
    public Specification<JenisSp> getSpecification() {
        Specification<JenisSp> namaSpec = Objects.isNull(nama) ? null :
                (root, query, cb) -> cb.equal(root.get("nama"), nama);
        return Specification.where(namaSpec);
    }

    public static JenisSp toEntity(JenisSpPostRequest request) {
        JenisSp entity = new JenisSp();
        entity.setNama(request.getNama());
        entity.setNotes(request.getNotes());
        return entity;
    }
}
