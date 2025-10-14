package id.perumdamts.kepegawaian.dto.master.level;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@Data
public class LevelPostRequest {
    private String nama;

    @JsonIgnore
    public Specification<Level> getSpecification() {
        return SpecificationBuilder.<Level>of()
                .addEqual(nama, "nama")
                .build();
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
