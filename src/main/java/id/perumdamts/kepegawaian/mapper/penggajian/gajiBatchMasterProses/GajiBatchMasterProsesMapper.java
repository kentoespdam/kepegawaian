package id.perumdamts.kepegawaian.mapper.penggajian.gajiBatchMasterProses;

import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesPostRequest;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;

public final class GajiBatchMasterProsesMapper {
    private GajiBatchMasterProsesMapper() {}

    public static GajiBatchMasterProses toEntity(GajiBatchMasterProsesPostRequest request) {
        GajiBatchMasterProses entity = new GajiBatchMasterProses();
        entity.setBatchMasterId(request.getBatchMasterId());
        entity.setKode("ADD_" + request.getNama().replace(" ", "_"));
        entity.setUrut(99);
        entity.setNama(request.getNama());
        entity.setJenisGaji(request.getJenisGaji());
        entity.setNilai(request.getNilai());
        return entity;
    }
}
