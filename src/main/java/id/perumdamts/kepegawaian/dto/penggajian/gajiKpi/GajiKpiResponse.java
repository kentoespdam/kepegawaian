package id.perumdamts.kepegawaian.dto.penggajian.gajiKpi;

import id.perumdamts.kepegawaian.entities.penggajian.GajiKpi;

import java.util.Objects;

public record GajiKpiResponse(
        Long id,
        String nipam,
        String periode,
        Double tunkin,
        Double pph21Ter
) {
    public static GajiKpiResponse from(GajiKpi entity) {
        if (Objects.isNull(entity)) return null;
        return new GajiKpiResponse(
                entity.getId(),
                entity.getNipam(),
                entity.getPeriode(),
                entity.getTunkin(),
                entity.getPph21Ter()
        );
    }
}
