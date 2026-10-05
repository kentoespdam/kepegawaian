package id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji;

import id.perumdamts.kepegawaian.entities.penggajian.DetailDasarGaji;
import lombok.Data;

@Data
public class DetailDasarGajiPostRequest {
    private Long dasarGajiId;
    private Integer mkg;
    private Long golonganId;
    private Double nominal;



}
