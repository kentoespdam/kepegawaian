package id.perumdamts.kepegawaian.mapper.penggajian.dasarGaji;

import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiPutRequest;
import id.perumdamts.kepegawaian.entities.penggajian.DasarGaji;

public final class DasarGajiMapper {
    private DasarGajiMapper() {}

    public static DasarGaji toEntity(DasarGajiPostRequest request) {
        DasarGaji dasarGaji = new DasarGaji();
        dasarGaji.setDeskripsi(request.getDeskripsi());
        dasarGaji.setTanggalAwal(request.getTanggalAwal());
        dasarGaji.setTanggalAkhir(request.getTanggalAkhir());
        dasarGaji.setAktif(request.getAktif());
        return dasarGaji;
    }

    public static void updateEntity(DasarGaji entity, DasarGajiPutRequest request) {
        entity.setDeskripsi(request.getDeskripsi());
        entity.setTanggalAwal(request.getTanggalAwal());
        entity.setTanggalAkhir(request.getTanggalAkhir());
        entity.setAktif(request.getAktif());
    }
}
