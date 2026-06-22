package id.perumdamts.kepegawaian.dto.master.hariLibur;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class HariLiburIndexQuery extends PagedRequest {
    private Integer tahun;
    private Integer bulan;
    private String jenisLibur;
}
