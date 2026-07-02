package id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DetailDasarGajiIndexQuery extends PagedRequest {
    private Long dasarGajiId;
    private Integer mkg;
    private Integer golonganKode;
    private Double nominal;
}
