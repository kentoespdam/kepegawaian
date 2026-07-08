package id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan;

import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import id.perumdamts.kepegawaian.mapper.master.level.LevelMapper;
import id.perumdamts.kepegawaian.entities.penggajian.GajiTunjangan;

import java.util.Objects;

public record GajiTunjanganResponse(
        Long id,
        EJenisTunjangan jenisTunjangan,
        LevelResponse level,
        GolonganResponse golongan,
        Double nominal
) {
    public static GajiTunjanganResponse from(GajiTunjangan entity) {
        return new GajiTunjanganResponse(
                entity.getId(),
                entity.getJenisTunjangan(),
                LevelMapper.toResponse(entity.getLevel()),
                Objects.nonNull(entity.getGolongan()) ? GolonganResponse.from(entity.getGolongan()) : null,
                entity.getNominal()
        );
    }
}
