package id.perumdamts.kepegawaian.dto.master.profesi;

import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProfesiResponse {
    private Long id;
    private LevelResponse level;
    private String nama;

    public static ProfesiResponse from(Profesi profesi) {
        ProfesiResponse response = new ProfesiResponse();
        response.setId(profesi.getId());
        LevelResponse levelResponse = LevelResponse.from(profesi.getLevel());
        response.setLevel(levelResponse);
        response.setNama(profesi.getNama());
        return response;
    }
}
