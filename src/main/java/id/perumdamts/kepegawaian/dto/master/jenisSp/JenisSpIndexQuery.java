package id.perumdamts.kepegawaian.dto.master.jenisSp;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JenisSpIndexQuery extends PagedRequest {
    private String kode;
    private String nama;


}
