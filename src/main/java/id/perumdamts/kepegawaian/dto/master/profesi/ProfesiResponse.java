package id.perumdamts.kepegawaian.dto.master.profesi;

import id.perumdamts.kepegawaian.dto.master.alatKerja.AlatKerjaMiniResponse;
import id.perumdamts.kepegawaian.dto.master.apd.ApdMiniResponse;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProfesiResponse {
    private Long id;
    private LevelResponse level;
    private String nama;
    private String detail;
    private String resiko;
    private List<ApdMiniResponse> apdList;
    private List<AlatKerjaMiniResponse> alatKerjaList;

    public static ProfesiResponse from(Profesi profesi) {
        ProfesiResponse response = new ProfesiResponse();
        response.setId(profesi.getId());
        LevelResponse levelResponse = LevelResponse.from(profesi.getLevel());
        response.setLevel(levelResponse);
        response.setNama(profesi.getNama());
        response.setDetail(profesi.getDetail());
        response.setResiko(profesi.getResiko());
        List<ApdMiniResponse> apdList = ApdMiniResponse.from(profesi.getApdList());
        response.setApdList(apdList);
        List<AlatKerjaMiniResponse> alatKerjaList = AlatKerjaMiniResponse.from(profesi.getAlatKerjaList());
        response.setAlatKerjaList(alatKerjaList);
        return response;
    }
}
