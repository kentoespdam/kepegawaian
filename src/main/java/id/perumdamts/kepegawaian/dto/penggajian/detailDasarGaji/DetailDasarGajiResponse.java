package id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji;

import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiResponse;
import id.perumdamts.kepegawaian.entities.penggajian.DetailDasarGaji;
import lombok.Data;

@Data
public class DetailDasarGajiResponse {
    private Long id;
    private DasarGajiResponse dasarGaji;
    private Integer mkg;
    private Integer golonganKode;
    private Double nominal;

    public static DetailDasarGajiResponse from(DetailDasarGaji entity){
        DetailDasarGajiResponse response = new DetailDasarGajiResponse();
        response.setId(entity.getId());
        response.setDasarGaji(DasarGajiResponse.from(entity.getDasarGaji()));
        response.setMkg(entity.getMkg());
        response.setGolonganKode(entity.getGolonganKode());
        response.setNominal(entity.getNominal());
        return response;
    }
}
