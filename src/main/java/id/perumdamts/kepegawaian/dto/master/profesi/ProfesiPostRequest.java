package id.perumdamts.kepegawaian.dto.master.profesi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@Data
public class ProfesiPostRequest {
    @Min(value = 1, message = "Level ID must be greater than or equal to 1")
    private Long levelId;
    @NotEmpty(message = "Nama Profesi is required")
    private String nama;
    @NotEmpty(message = "Detail Profesi is required")
    private String detail;
    @NotEmpty(message = "Resiko Profesi is required")
    private String resiko;

    @JsonIgnore
    public Specification<Profesi> getSpecification() {
        Specification<Profesi> levelSpec = Objects.isNull(levelId) ? null :
                (root, query, cb) -> cb.equal(root.get("level").get("id"), levelId);
        Specification<Profesi> namaSpec = Objects.isNull(nama) ? null :
                (root, query, cb) -> cb.equal(root.get("nama"), nama);
        return Specification.where(levelSpec).and(namaSpec);
    }

    public static Profesi toEntity(ProfesiPostRequest request, Level level) {
        Profesi entity = new Profesi();
        entity.setLevel(level);
        entity.setNama(request.getNama());
        entity.setDetail(request.getDetail());
        entity.setResiko(request.getResiko());
        return entity;
    }
}
