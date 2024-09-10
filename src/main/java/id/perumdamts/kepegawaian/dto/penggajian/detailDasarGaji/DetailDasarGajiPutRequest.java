package id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji;

import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.penggajian.DasarGaji;
import id.perumdamts.kepegawaian.entities.penggajian.DetailDasarGaji;

public class DetailDasarGajiPutRequest extends DetailDasarGajiPostRequest {
    public static DetailDasarGaji toEntity(DetailDasarGaji entity, DetailDasarGajiPutRequest request, DasarGaji dasarGaji, Golongan golongan) {
        Integer golonganKode = Integer.parseInt(golongan.getGolongan().split("\\.")[1]);
        entity.setDasarGaji(dasarGaji);
        entity.setGolonganKode(golonganKode);
        entity.setMkg(request.getMkg());
        entity.setNominal(request.getNominal());
        return entity;
    }
}
