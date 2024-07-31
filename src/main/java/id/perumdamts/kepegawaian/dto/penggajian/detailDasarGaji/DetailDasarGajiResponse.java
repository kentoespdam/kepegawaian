package id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji;

import id.perumdamts.kepegawaian.dto.master.golongan.GolonganResponse;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiResponse;
import id.perumdamts.kepegawaian.entities.penggajian.DetailDasarGaji;
import lombok.Data;

@Data
public class DetailDasarGajiResponse {
    private Long id;
    private DasarGajiResponse dasarGaji;
    private Integer mkg;
    private GolonganResponse golongan;
    private Integer nominal;

    public static DetailDasarGajiResponse from(DetailDasarGaji entity){
        DetailDasarGajiResponse response = new DetailDasarGajiResponse();
        response.setId(entity.getId());
        response.setDasarGaji(DasarGajiResponse.from(entity.getDasarGaji()));
        response.setMkg(entity.getMkg());
        response.setGolongan(GolonganResponse.from(entity.getGolongan()));
        response.setNominal(entity.getNominal());
        return response;
    }
}
