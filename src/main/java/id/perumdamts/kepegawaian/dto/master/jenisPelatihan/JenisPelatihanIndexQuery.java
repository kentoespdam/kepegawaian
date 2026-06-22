package id.perumdamts.kepegawaian.dto.master.jenisPelatihan;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JenisPelatihanIndexQuery extends PagedRequest {
    private String nama;
}
