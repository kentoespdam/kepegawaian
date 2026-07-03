package id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk;

import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPotonganTkk;
import id.perumdamts.kepegawaian.mapper.master.level.LevelMapper;
import lombok.Data;

@Data
public class GajiPotonganTkkResponse {
    private Long id;
    private EStatusPegawai statusPegawai;
    private LevelResponse level;
    private GolonganResponse golongan;
    private Double nominal;

    public static GajiPotonganTkkResponse from(GajiPotonganTkk entity) {
        GajiPotonganTkkResponse response = new GajiPotonganTkkResponse();
        response.setId(entity.getId());
        response.setStatusPegawai(entity.getStatusPegawai());
        if (entity.getLevel() != null)
            response.setLevel(LevelMapper.toResponse(entity.getLevel()));
        if (entity.getGolongan() != null)
            response.setGolongan(GolonganResponse.from(entity.getGolongan()));
        response.setNominal(entity.getNominal());
        return response;
    }
}
