package id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji;

import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiResponse;
import id.perumdamts.kepegawaian.entities.penggajian.DetailDasarGaji;

public record DetailDasarGajiResponse(
        Long id,
        DasarGajiResponse dasarGaji,
        Integer mkg,
        Integer golonganKode,
        Double nominal
) {
    public static DetailDasarGajiResponse from(DetailDasarGaji entity) {
        return new DetailDasarGajiResponse(
                entity.getId(),
                DasarGajiResponse.from(entity.getDasarGaji()),
                entity.getMkg(),
                entity.getGolonganKode(),
                entity.getNominal()
        );
    }
}
