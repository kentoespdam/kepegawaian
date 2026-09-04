package id.perumdamts.kepegawaian.dto.penggajian.gajiKpi;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiKpiIndexQuery extends PagedRequest {
    private String nipam;
    private String periode;
}
