package id.perumdamts.kepegawaian.dto.master.jabatan;

import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import lombok.Data;

@Data
public class JabatanMiniResponse {
    private Long id;
    private String kode;
    private LevelResponse level;
    private String nama;

    public static JabatanMiniResponse from(Jabatan entity) {
        if (entity == null) return null;
        JabatanMiniResponse response = new JabatanMiniResponse();
        response.setId(entity.getId());
        response.setKode(entity.getKode());
        response.setLevel(LevelResponse.from(entity.getLevel()));
        response.setNama(entity.getNama());
        return response;
    }
}
