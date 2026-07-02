package id.perumdamts.kepegawaian.dto.penggajian.gajiProfil;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiProfilIndexQuery extends PagedRequest {
    private String nama;
}
