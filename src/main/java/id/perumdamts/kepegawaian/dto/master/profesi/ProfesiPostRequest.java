package id.perumdamts.kepegawaian.dto.master.profesi;

import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ProfesiPostRequest {
    @Min(value = 1, message = "Level ID must be greater than or equal to 1")
    private Long levelId;
    @NotEmpty(message = "Nama Profesi is required")
    private String nama;

    public static Profesi toEntity(ProfesiPostRequest request) {
        Profesi entity = new Profesi();
        entity.setLevel(new Level(request.getLevelId()));
        entity.setNama(request.getNama());
        return entity;
    }

    public static Profesi toEntity(ProfesiPostRequest request, Long id) {
        Profesi entity = new Profesi(id);
        entity.setLevel(new Level(request.getLevelId()));
        entity.setNama(request.getNama());
        return entity;
    }

    public static List<Profesi> toEntities(List<ProfesiPostRequest> requests) {
        return requests.stream().map(ProfesiPostRequest::toEntity).toList();
    }
}
