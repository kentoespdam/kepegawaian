package id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiPendapatanNonPajakIndexQuery extends PagedRequest {
    private String kode;
}
