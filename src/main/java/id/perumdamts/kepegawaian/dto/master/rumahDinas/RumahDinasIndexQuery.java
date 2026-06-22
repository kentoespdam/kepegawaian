package id.perumdamts.kepegawaian.dto.master.rumahDinas;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RumahDinasIndexQuery extends PagedRequest {
    private String nama;
}
