package id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk;

import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPotonganTkk;
import id.perumdamts.kepegawaian.mapper.master.level.LevelMapper;

import java.util.Objects;

public record GajiPotonganTkkResponse(
        Long id,
        EStatusPegawai statusPegawai,
        LevelResponse level,
        GolonganResponse golongan,
        Double nominal
) {
    public static GajiPotonganTkkResponse from(GajiPotonganTkk entity) {
        return new GajiPotonganTkkResponse(
                entity.getId(),
                entity.getStatusPegawai(),
                Objects.nonNull(entity.getLevel()) ? LevelMapper.toResponse(entity.getLevel()) : null,
                Objects.nonNull(entity.getGolongan()) ? GolonganResponse.from(entity.getGolongan()) : null,
                entity.getNominal()
        );
    }
}
