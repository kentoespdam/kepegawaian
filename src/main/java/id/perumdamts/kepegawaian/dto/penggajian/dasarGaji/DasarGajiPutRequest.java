package id.perumdamts.kepegawaian.dto.penggajian.dasarGaji;

import id.perumdamts.kepegawaian.entities.penggajian.DasarGaji;

public class DasarGajiPutRequest extends DasarGajiPostRequest {
    public static DasarGaji toEntity(DasarGaji entity, DasarGajiPutRequest request) {
        entity.setDeskripsi(request.getDeskripsi());
        entity.setTglAwal(request.getTanggalAwal());
        entity.setTglAkhir(request.getTanggalAkhir());
        entity.setAktif(request.getAktif());
        return entity;
    }
}
