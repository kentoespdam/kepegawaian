package id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiPhdpIndexQuery extends PagedRequest {
    private String kondisi;
}
