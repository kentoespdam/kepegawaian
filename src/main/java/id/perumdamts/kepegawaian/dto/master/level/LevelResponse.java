package id.perumdamts.kepegawaian.dto.master.level;

import id.perumdamts.kepegawaian.entities.master.Level;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LevelResponse {
    private Long id;
    private String nama;

    public static LevelResponse from(Level level) {
        return new LevelResponse(level.getId(), level.getNama());
    }
}
