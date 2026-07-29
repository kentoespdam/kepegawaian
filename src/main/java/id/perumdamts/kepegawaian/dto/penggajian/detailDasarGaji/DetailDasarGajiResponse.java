package id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji;

import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiMiniResponse;

public record DetailDasarGajiResponse(
        Long id,
        DasarGajiMiniResponse dasarGaji,
        Integer mkg,
        Integer golonganKode,
        Double nominal
) {
}
