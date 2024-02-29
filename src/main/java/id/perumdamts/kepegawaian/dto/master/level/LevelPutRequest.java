package id.perumdamts.kepegawaian.dto.master.level;

import id.perumdamts.kepegawaian.entities.master.Level;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LevelPutRequest extends LevelPostRequest{
    public static Level toEntity(LevelPutRequest request, Long id) {
        return new Level(id, request.getNama());
    }
}
