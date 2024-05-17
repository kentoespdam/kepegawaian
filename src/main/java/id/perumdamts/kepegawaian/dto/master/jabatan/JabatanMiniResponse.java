package id.perumdamts.kepegawaian.dto.master.jabatan;

import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import lombok.Data;

import java.util.Objects;

@Data
public class JabatanMiniResponse {
    private Long id;
    private JabatanMiniResponse parent;
    private LevelResponse level;
    private String nama;

    public static JabatanMiniResponse from(Jabatan entity) {
        JabatanMiniResponse response = new JabatanMiniResponse();
        response.setId(entity.getId());
        if (Objects.nonNull(entity.getParent()))
            response.setParent(JabatanMiniResponse.from(entity));
        response.setLevel(LevelResponse.from(entity.getLevel()));
        response.setNama(entity.getNama());
        return response;
    }
}
