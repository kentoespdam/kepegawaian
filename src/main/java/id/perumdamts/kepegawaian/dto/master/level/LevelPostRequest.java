package id.perumdamts.kepegawaian.dto.master.level;

import id.perumdamts.kepegawaian.entities.master.Level;
import lombok.Data;

import java.util.List;

@Data
public class LevelPostRequest {
    private String nama;

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
