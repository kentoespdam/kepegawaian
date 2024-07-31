package id.perumdamts.kepegawaian.dto.master.jenisSk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.JenisSk;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@Data
public class JenisSkPostRequest {
    @NotEmpty(message = "Nama is required")
    private String nama;
    private String notes;

    @JsonIgnore
    public Specification<JenisSk> getSpecification() {
        Specification<JenisSk> namaSpec = Objects.isNull(nama) ? null :
                (root, query, cb) -> cb.equal(root.get("nama"), nama);
        return Specification.where(namaSpec);
    }

    public static JenisSk toEntity(JenisSkPostRequest request) {
        JenisSk entity = new JenisSk();
        entity.setNama(request.getNama());
        entity.setNotes(request.getNotes());
        return entity;
    }
}
