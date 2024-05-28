package id.perumdamts.kepegawaian.dto.master.level;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.Level;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;

@Data
public class LevelPostRequest {
    private String nama;

    @JsonIgnore
    public Specification<Level> getSpecification() {
        Specification<Level> namaSpec = Objects.isNull(nama) ? null :
                (root, query, cb) -> cb.equal(root.get("nama"), nama);
        return Specification.where(namaSpec);
    }

    public static Level toEntity(LevelPostRequest request) {
        return new Level(request.getNama());
    }

    public static Level toEntity(LevelPostRequest request, Long id) {
        return new Level(id, request.getNama());
    }

    public static List<Level> toEntities(List<LevelPostRequest> requests) {
        return requests.stream().map(LevelPostRequest::toEntity).toList();
    }
}
