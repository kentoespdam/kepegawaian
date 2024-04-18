package id.perumdamts.kepegawaian.dto.master.jenjangPendidikan;

import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;

@Data
public class JenjangPendidikanPostRequest {
    @NotEmpty(message = "Nama is required")
    private String nama;
    @Min(value = 1, message = "Seq is required")
    private Integer seq;

    public static JenjangPendidikan toEntity(JenjangPendidikanPostRequest request) {
        return new JenjangPendidikan(request.getNama(), request.getSeq());
    }

    public static List<JenjangPendidikan> toEntities(List<JenjangPendidikanPostRequest> requests) {
        return requests.stream().map(JenjangPendidikanPostRequest::toEntity).toList();
    }

    public Specification<JenjangPendidikan> getSpecification() {
        Specification<JenjangPendidikan> namaSpec = Objects.isNull(nama) ? null :
                (root, query, cb) -> cb.equal(root.get("nama"), nama);
        return Specification.where(namaSpec);
    }
}
