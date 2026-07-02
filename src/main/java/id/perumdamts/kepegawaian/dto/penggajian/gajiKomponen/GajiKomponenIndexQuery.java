package id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiKomponenIndexQuery extends PagedRequest {
    private Long profilId;
    private String kode;
}
