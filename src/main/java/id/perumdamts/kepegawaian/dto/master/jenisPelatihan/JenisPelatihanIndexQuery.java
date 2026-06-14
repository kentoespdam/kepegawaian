package id.perumdamts.kepegawaian.dto.master.jenisPelatihan;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JenisPelatihanIndexQuery extends CommonPageRequest {
    private String nama;
}
