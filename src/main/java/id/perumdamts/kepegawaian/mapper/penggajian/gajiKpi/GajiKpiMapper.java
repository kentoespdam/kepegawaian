package id.perumdamts.kepegawaian.mapper.penggajian.gajiKpi;

import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiPutRequest;
import id.perumdamts.kepegawaian.entities.penggajian.GajiKpi;

public final class GajiKpiMapper {
    private GajiKpiMapper() {}

    public static GajiKpi toEntity(GajiKpiPostRequest request) {
        GajiKpi entity = new GajiKpi();
        entity.setNipam(request.getNipam());
        entity.setPeriode(request.getPeriode());
        entity.setTunkin(request.getTunkin());
        entity.setPph21Ter(request.getPph21Ter());
        return entity;
    }

    public static void updateEntity(GajiKpi entity, GajiKpiPutRequest request) {
        entity.setNipam(request.getNipam());
        entity.setPeriode(request.getPeriode());
        entity.setTunkin(request.getTunkin());
        entity.setPph21Ter(request.getPph21Ter());
    }
}
