package id.perumdamts.kepegawaian.dto.master.sanksi;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SanksiIndexQuery extends PagedRequest {
    private String kode;
    private String keterangan;
    private Long jenisSpId;


}
