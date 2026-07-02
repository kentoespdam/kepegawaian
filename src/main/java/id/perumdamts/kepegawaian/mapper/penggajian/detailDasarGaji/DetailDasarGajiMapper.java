package id.perumdamts.kepegawaian.mapper.penggajian.detailDasarGaji;

import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiPutRequest;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.penggajian.DasarGaji;
import id.perumdamts.kepegawaian.entities.penggajian.DetailDasarGaji;

public final class DetailDasarGajiMapper {
    private DetailDasarGajiMapper() {}

    public static DetailDasarGaji toEntity(DetailDasarGajiPostRequest request, DasarGaji dasarGaji, Golongan golongan) {
        Integer golonganKode = Integer.parseInt(golongan.getGolongan().split("\\.")[1]);
        DetailDasarGaji entity = new DetailDasarGaji();
        entity.setDasarGaji(dasarGaji);
        entity.setMkg(request.getMkg());
        entity.setGolonganKode(golonganKode);
        entity.setNominal(request.getNominal());
        return entity;
    }

    public static void updateEntity(DetailDasarGaji entity, DetailDasarGajiPutRequest request, DasarGaji dasarGaji, Golongan golongan) {
        Integer golonganKode = Integer.parseInt(golongan.getGolongan().split("\\.")[1]);
        entity.setDasarGaji(dasarGaji);
        entity.setGolonganKode(golonganKode);
        entity.setMkg(request.getMkg());
        entity.setNominal(request.getNominal());
    }
}
